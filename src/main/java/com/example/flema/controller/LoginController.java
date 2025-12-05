// コントローラのパッケージ宣言
package com.example.fleamarketsystem.controller;

// MVC コントローラのアノテーション
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// MVC コントローラとして登録
@Controller
public class LoginController {
  // ログインページ表示のハンドラ
  @GetMapping("/login")
  public String login() {
    // login.html（Thymeleaf）を返す
    return "login";
  }
}
