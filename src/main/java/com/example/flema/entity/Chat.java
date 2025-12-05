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
 * 商品ごとのチャットメッセージを表すエンティティ
 */
@Entity
@Table(name = "chat")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chat {

  /** 主キー */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** 対象商品（外部キー item_id, NOT NULL） */
  @ManyToOne
  @JoinColumn(name = "item_id", nullable = false)
  private Item item;

  /** 送信者ユーザ（外部キー sender_id, NOT NULL） */
  @ManyToOne
  @JoinColumn(name = "sender_id", nullable = false)
  private User sender;

  /** メッセージ本文（TEXT） */
  @Column(columnDefinition = "TEXT", nullable = false)
  private String message;

  /** 作成日時 */
  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();
}
