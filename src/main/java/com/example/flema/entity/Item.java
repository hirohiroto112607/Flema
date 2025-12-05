package com.example.flema.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 出品された商品を表すエンティティ
 */
@Entity
@Table(name = "item")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    /** 主キー */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 出品者（users テーブルへの外部キー user_id, NOT NULL） */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User seller;

    /** 商品名（NOT NULL） */
    @Column(nullable = false)
    private String name;

    /** 商品説明（TEXT） */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    /** 価格（NOT NULL, 小数対応） */
    @Column(nullable = false)
    private BigDecimal price;

    /** カテゴリ（外部キー category_id, NULL 許可 = 未分類OK） */
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    /** 出品ステータス（例: 出品中／売却済み 等） */
    @Column(nullable = false)
    private String status = "出品中";

    /** 画像URL（Cloudinary など外部ストレージの URL を想定） */
    private String imageUrl;

    /** 作成日時 */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
