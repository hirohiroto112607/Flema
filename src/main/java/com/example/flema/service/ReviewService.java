
package com.example.flema.service;

import com.example.flema.entity.AppOrder;
import com.example.flema.entity.Review;
import com.example.flema.entity.User;
import com.example.flema.repository.AppOrderRepository;
import com.example.flema.repository.ReviewRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.OptionalDouble;

@Service
public class ReviewService {
    
    private final ReviewRepository reviewRepository;
    private final AppOrderRepository appOrderRepository;
    
    public ReviewService(
        ReviewRepository reviewRepository,
        AppOrderRepository appOrderRepository) {
        this.reviewRepository = reviewRepository;
        this.appOrderRepository = appOrderRepository;
    }

    // レビュー投稿（買い手のみ、1注文1レビュー）
    @Transactional
    public Review submitReview(
        Long orderId,
        User reviewer,
        int rating,
        String comment) {
            // 注文を取得（存在しなければ400相当）
            AppOrder order = appOrderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found."));
            // 注文の買い手と同一ユーザか検証

            if (!order.getBuyer().getId().equals(reviewer.getId())) {
                // 買い手以外は拒否
                throw new IllegalStateException("Only the buyer can review this order.");
            }

            // 既にレビュー済みかを検査
            if (reviewRepository.findByOrderId(orderId).isPresent()) {
                // 二重レビューを防ぐ
                throw new IllegalStateException("This order has already been reviewed.");
            }
            // 新しいレビューエンティティを構築
            Review review = new Review();

            // 注文、レビュワー、出品者、対象商品、評価点、コメントを紐付け
            review.setOrder(order);
            review.setReviewer(reviewer);
            review.setSeller(order.getItem().getSeller());
            review.setItem(order.getItem());
            review.setRating(rating);
            review.setComment(comment);
            
            // 保存して返却
            return reviewRepository.save(review);
    }

    // 出品者に対するレビュー一覧を取得
    public List<Review> getReviewsBySeller(User seller) {
        // リポジトリに委譲
        return reviewRepository.findBySeller(seller);
    }

    // 出品者に対する平均評価を算出
    public OptionalDouble getAverageRatingForSeller(User seller) {
        // ストリームで平均を計算
        return reviewRepository.findBySeller(seller).stream()
        .mapToInt(Review::getRating)
        .average();
    }

    // あるレビュワーが書いたレビューを取得
    public List<Review> getReviewsByReviewer(User reviewer) {
        // リポジトリに委譲
        return reviewRepository.findByReviewer(reviewer);
    }
}