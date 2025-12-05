package com.example.flema.service;

import com.example.flema.entity.Chat;
import com.example.flema.entity.Item;
import com.example.flema.entity.User;
import com.example.flema.repository.ChatRepository;
import com.example.flema.repository.ItemRepository;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final ItemRepository itemRepository;
    private final LineNotifyService lineNotifyService;

    public ChatService(
        ChatRepository chatRepository,
        ItemRepository itemRepository,
        LineNotifyService lineNotifyService) {
        this.chatRepository = chatRepository;
        this.itemRepository = itemRepository;
        this.lineNotifyService = lineNotifyService;
    }

    // 商品IDに紐づくチャット履歴を昇順で取得
    public List<Chat> getChatMessagesByItem(Long itemId) {
        // 商品の存在を確認（なければ400相当の例外）
        Item item = itemRepository.findById(itemId)
        .orElseThrow(() -> new IllegalArgumentException("Item not found"));
        // 作成日時昇順でリストを返す
        return chatRepository.findByItemByCreatedAtAsc(item);
    }

    // メッセージ送信：保存して相手にLINE通知（可能なら）を行う
    public Chat sendMessage(Long itemId, User sender, String message) {
        // 対象商品を取得（存在しなければ例外）
        Item item = itemRepository.findById(itemId)
        .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        // 新規チャットエンティティを構築
        Chat chat = new Chat();

        // 商品、送信者、本文、現在時刻を送信時刻に紐づけ
        chat.setItem(item);
        chat.setSender(sender);
        chat.setMessage(message);
        chat.setCreatedAt(LocalDateTime.now());

        // 保存して永続化
        Chat savedChat = chatRepository.save(chat);

        // 簡易実装：受信者を出品者とみなして通知（詳細な相手判定は拡張で対応）
        User receiver = item.getSeller();

        // 受信者が通知トークンを設定していれば通知を送る
        if (receiver != null && receiver.getLineNotifyToken() != null) {
            // 通知本文を作成
            String notificationMessage = String.format("\n商品「%s」に関する新しいメッセージが届きました！\n送信者: %s\nメッセージ: %s",
            item.getName(),
            sender.getName(),
            message);

            // LINE Notifyへ送信
            lineNotifyService.sendMessage(receiver.getLineNotifyToken(), notificationMessage);
        }
        // 保存結果を返却
        return savedChat;
    }
}
