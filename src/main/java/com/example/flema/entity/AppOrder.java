package com.example.flema.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "app_order")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppOrder {

  /** 主キー */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** 注文が紐づく商品（必須） */
  @ManyToOne
  @JoinColumn(name = "item_id", nullable = false)
  private Item item;

  /** 買い手ユーザ（必須） */
  @ManyToOne
  @JoinColumn(name = "buyer_id", nullable = false)
  private User buyer;

  /** 決済金額スナップショット（必須） */
  @Column(nullable = false)
  private BigDecimal price;

  /** 注文状態（購入済／発送済／決済待ち 等） */
  @Column(nullable = false)
  private String status = "購入済";

  /** Stripe の PaymentIntent ID（決済と注文を 1 対 1 で特定） */
  @Column(name = "payment_intent_id", unique = true)
  private String paymentIntentId;

  /** 作成日時（集計用） */
  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();
}
