package com.example.flema.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.hibernate.cache.spi.support.AbstractReadWriteAccess.Item;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.flema.repository.AppOrderRepository;
import com.example.flema.repository.ItemRepository;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

@Service
public class AppOrderService {

    private final AppOrderRepository appOrderRepository;
    private final ItemRepository itemRepository;
    private final ItemService itemService;
    private final StripeService stripeService;
    private final LineNotifyService lineNotifyService;

    public AppOrderService(
        AppOrderRepository appOrderRepository,
        ItemRepository itemRepository,
        ItemService itemService,
        StripeService stripeService,
        LineNotifyService lineNotifyService
    ) {
        this.appOrderRepository = appOrderRepository;
        this.itemRepository = itemRepository;
        this.itemService = itemService;
        this.stripeService = stripeService;
        this.lineNotifyService = lineNotifyService;
    }

    @Transactional
    public PaymentIntent initiatePurchase(Long itemId, User buyer) throws StripeException {
        // 商品を取得（なければ400）
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("Item not found"));
        // すでに売却済みならエラー
        if (!"出品中".equals(item.getStatus())) {
            throw new IllegalStateException("Item is not available for purchase.");
        }
        // StripeへPaymentIntent作成（JPYは最小単位が1円のためcreate側で考慮）
        PaymentIntent paymentIntent = stripeService.createPaymentIntent(item.getPrice(),"jpy", "購入: " + item.getName());
        // 注文を“決済待ち”で作成し、PaymentIntent IDを確実に保存
        AppOrder appOrder = new AppOrder();

        // 商品、買い手、金額を紐付け
        appOrder.setItem(item);
        appOrder.setBuyer(buyer);
        appOrder.setPrice(item.getPrice());

        // ステータスを決済待ちへ
        appOrder.setStatus("決済待ち");

        // PaymentIntent IDを保存（これで後続完了時に1件特定できる）
        appOrder.setPaymentIntentId(paymentIntent.getId());
        // 作成日時
        appOrder.setCreatedAt(LocalDateTime.now());

        // DBへ保存
        appOrderRepository.save(appOrder);

        // フロントへclient_secret等を返すためIntentを返却
        return paymentIntent;
    }


    // 決済完了：PaymentIntent IDで1件を厳密に取得して確定処理@Transactional
    public AppOrder completePurchase(String paymentIntentId) throws StripeException {
        // StripeからIntentの最新状態を取得
        PaymentIntent paymentIntent = stripeService.retrievePaymentIntent(paymentIntentId);
        // 成功以外はエラー
        if (!"succeeded".equals(paymentIntent.getStatus())) {
            throw new IllegalStateException(
                "Payment not succeeded. Status: " +
                paymentIntent.getStatus()
            );
        }
        // 保存済みの注文をPaymentIntent IDで1件特定（ここが安全化の肝）
        AppOrder appOrder = appOrderRepository
        .findByPaymentIntentId(paymentIntentId)
        .orElseThrow(() -> new IllegalStateException("Order for PaymentIntent notfound."));
        // 既に確定済みなら冪等に成功扱い
        if ("購入済".equals(appOrder.getStatus()) || "発送済".equals(appOrder.getStatus())) {
        // そのまま返す（再通知などはしない）
            return appOrder;
        }
        // ステータスを購入済へ
        appOrder.setStatus("購入済");
        // 商品を売却済みに更新（在庫1想定）
        itemService.markItemAsSold(appOrder.getItem().getId());
        // 保存
        AppOrder savedOrder = appOrderRepository.save(appOrder);
        // 売り手がLine通知トークンを持っていれば通知
        if (savedOrder.getItem().getSeller().getLineNotifyToken() != null) {
            String message = String.format("\n商品が購入されました!\n商品名: %s\n購入者: %s\n価格: ¥%s",
            savedOrder.getItem().getName(),
            savedOrder.getBuyer().getName(),
            savedOrder.getPrice());
            // 例外は内側で処理してログ出し
            lineNotifyService.sendMessage(savedOrder.getItem().getSeller().getLineNotifyToken(), message);
        }
        // 確定した注文を返す
        return savedOrder;
    }
        
    // すべての注文取得（管理者ダッシュボード等）
    public List<AppOrder> getAllOrders() {
        // 全件を返す
        return appOrderRepository.findAll();
    }

    // 買い手別の注文一覧
    public List<AppOrder> getOrdersByBuyer(User buyer) {
        // リポジトリ委譲
        return appOrderRepository.findByBuyer(buyer);
    }

    // 売り手別の注文一覧
    public List<AppOrder> getOrdersBySeller(User seller) {
        // リポジトリ委譲
        return appOrderRepository.findByItem_Seller(seller);
    }

    // 発送処理：ステータスと通知
    @Transactional
    public void markOrderAsShipped(Long orderId) {
        // 注文取得（なければ404相当）
        AppOrder appOrder = appOrderRepository.findById(orderId)
        .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        // ステータス更新
        appOrder.setStatus("発送済");
        // 保存
        AppOrder savedOrder = appOrderRepository.save(appOrder);
        // 買い手にLINE通知があれば送信
        if (savedOrder.getBuyer().getLineNotifyToken() != null) {
            String message = String.format("\n購入した商品が発送されました!\n商品名: %s\n出品者: %s",
            savedOrder.getItem().getName(),
            savedOrder.getItem().getSeller().getName());
            // 送信試行（失敗はログのみ）
            lineNotifyService.sendMessage(savedOrder.getBuyer().getLineNotifyToken(), message);
        }
    }
    // IDで1件取得
    public Optional<AppOrder> getOrderById(Long orderId) {
        // Optionalで返す
        return appOrderRepository.findById(orderId);
    }
    
    // 最新の“購入済”注文ID（レビュー画面遷移用）
    public Optional<Long> getLatestCompletedOrderId() {
        // ステータスが購入済の中で最大IDを返す
        return appOrderRepository.findAll().stream()
        .filter(o -> "購入済".equals(o.getStatus()))
        .map(AppOrder::getId)
        .max(Long::compare);
    }
    
    // 指定期間の売上合計
    public BigDecimal getTotalSales(LocalDate startDate, LocalDate endDate) {
        // 期間内の購入済/発送済のみ合計
        return appOrderRepository.findAll().stream()
        .filter(order ->
            order.getStatus().equals("購入済") ||
            order.getStatus().equals("発送済")
        )
        .filter(order ->
            order.getCreatedAt().toLocalDate().isAfter(startDate.minusDays(1)) &&
            order.getCreatedAt().toLocalDate().isBefore(endDate.plusDays(1)))
        .map(AppOrder::getPrice)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    // 指定期間のステータス別件数
    public Map<String, Long> getOrderCountByStatus(LocalDate startDate, LocalDate endDate) {
        // 作成日で期間フィルタしてグルーピング
        return appOrderRepository.findAll().stream()
        .filter(order ->
            order.getCreatedAt().toLocalDate().isAfter(startDate.minusDays(1)) &&
            order.getCreatedAt().toLocalDate().isBefore(endDate.plusDays(1)))
        .collect(Collectors.groupingBy(AppOrder::getStatus, Collectors.counting()));
    }
}