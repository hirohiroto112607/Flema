package com.example.flema.controller;

import com.example.flema.entity.User;
import com.example.flema.service.AppOrderService;
import com.example.flema.service.FavoriteService;
import com.example.flema.service.ItemService;
import com.example.flema.service.ReviewService;
import com.example.flema.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/my-page")
public class UserController {

  private final UserService userService;
  private final ItemService itemService;
  private final AppOrderService appOrderService;
  private final FavoriteService favoriteService;
  private final ReviewService reviewService;

  public UserController(UserService userService,
      ItemService itemService,
      AppOrderService appOrderService,
      FavoriteService favoriteService,
      ReviewService reviewService) {
    this.userService = userService;
    this.itemService = itemService;
    this.appOrderService = appOrderService;
    this.favoriteService = favoriteService;
    this.reviewService = reviewService;
  }

  /** マイページ（プロフィール）トップ */
  @GetMapping
  public String myPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {

    User currentUser = userService.getUserByEmail(userDetails.getUsername())
        .orElseThrow(() -> new RuntimeException("User not found"));

    model.addAttribute("user", currentUser);

    return "my_page";
  }

  /** 自分の出品一覧 */
  @GetMapping("/selling")
  public String mySellingItems(@AuthenticationPrincipal UserDetails userDetails, Model model) {

    User currentUser = userService.getUserByEmail(userDetails.getUsername())
        .orElseThrow(() -> new RuntimeException("User not found"));

    model.addAttribute("sellingItems", itemService.getItemsBySeller(currentUser));

    return "seller_items";
  }

  /** 自分が購入した注文一覧 */
  @GetMapping("/orders")
  public String myOrders(@AuthenticationPrincipal UserDetails userDetails, Model model) {

    User currentUser = userService.getUserByEmail(userDetails.getUsername())
        .orElseThrow(() -> new RuntimeException("User not found"));

    model.addAttribute("myOrders", appOrderService.getOrdersByBuyer(currentUser));

    return "buyer_app_orders";
  }

  /** 自分が販売した注文一覧（売上） */
  @GetMapping("/sales")
  public String mySales(@AuthenticationPrincipal UserDetails userDetails, Model model) {

    User currentUser = userService.getUserByEmail(userDetails.getUsername())
        .orElseThrow(() -> new RuntimeException("User not found"));

    model.addAttribute("mySales", appOrderService.getOrdersBySeller(currentUser));

    return "seller_app_orders";
  }

  /** 自分のお気に入り商品一覧 */
  @GetMapping("/favorites")
  public String myFavorites(@AuthenticationPrincipal UserDetails userDetails, Model model) {

    User currentUser = userService.getUserByEmail(userDetails.getUsername())
        .orElseThrow(() -> new RuntimeException("User not found"));

    model.addAttribute(
        "favoriteItems",
        favoriteService.getFavoriteItemsByUser(currentUser));

    return "my_favorites";
  }

  /** 自分が書いたレビュー一覧 */
  @GetMapping("/reviews")
  public String myReviews(@AuthenticationPrincipal UserDetails userDetails, Model model) {

    User currentUser = userService.getUserByEmail(userDetails.getUsername())
        .orElseThrow(() -> new RuntimeException("User not found"));

    model.addAttribute("reviews", reviewService.getReviewsByReviewer(currentUser));

    return "user_reviews";
  }
}
