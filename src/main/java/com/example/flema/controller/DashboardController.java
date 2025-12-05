package com.example.flema.controller;

import com.example.flema.entity.User;
import com.example.flema.repository.UserRepository;
import com.example.flema.service.AppOrderService;
import com.example.flema.service.ItemService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

  private final UserRepository userRepository;
  private final ItemService itemService;
  private final AppOrderService appOrderService;

  public DashboardController(UserRepository userRepository,
      ItemService itemService,
      AppOrderService appOrderService) {
    this.userRepository = userRepository;
    this.itemService = itemService;
    this.appOrderService = appOrderService;
  }

  /** ダッシュボード画面のハンドラ */
  @GetMapping("/dashboard")
  public String dashboard(@AuthenticationPrincipal UserDetails userDetails,
      Model model) {

    // ログインユーザをメールから検索
    User currentUser = userRepository.findByEmail(userDetails.getUsername())
        .orElseThrow(() -> new RuntimeException("User not found"));

    // 管理者なら管理ダッシュボードを表示
    if ("ADMIN".equals(currentUser.getRole())) {

      // 最近の商品（ここでは全件）
      model.addAttribute("recentItems", itemService.getAllItems());

      // 最近の注文（ここでは全件）
      model.addAttribute("recentOrders", appOrderService.getAllOrders());

      return "admin_dashboard";
    } else {
      // 一般ユーザは商品一覧へリダイレクト
      return "redirect:/items";
    }
  }
}
