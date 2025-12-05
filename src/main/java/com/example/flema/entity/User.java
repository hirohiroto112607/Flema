package com.example.flema.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 会員ユーザを表すエンティティ
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

  /** 主キー */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** 表示名（必須） */
  @Column(nullable = false)
  private String name;

  /** ログインIDとして使用するメールアドレス（ユニーク） */
  @Column(unique = true, nullable = false)
  private String email;

  /** ハッシュ化済みパスワード（必須） */
  @Column(nullable = false)
  private String password;

  /** 役割（例: USER / ADMIN）（必須） */
  @Column(nullable = false)
  private String role;

  /** LINE Notify のアクセストークン（任意） */
  @Column(name = "line_notify_token")
  private String lineNotifyToken;

  /** アカウント有効フラグ（初期値 true） */
  @Column(nullable = false)
  private boolean enabled = true;
}
