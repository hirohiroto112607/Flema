package com.example.flema.entity;

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
 * ユーザのお気に入り商品を表すエンティティ
 */
@Entity
@Table(name = "favorite_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteItem {

  /** 主キー */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** お気に入り登録したユーザ（外部キー user_id, NOT NULL） */
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  /** お気に入り対象の商品（外部キー item_id, NOT NULL） */
  @ManyToOne
  @JoinColumn(name = "item_id", nullable = false)
  private Item item;

  /** お気に入り登録日時 */
  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();
}
