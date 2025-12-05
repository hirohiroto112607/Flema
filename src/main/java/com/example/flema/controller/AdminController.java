package com.example.flema.controller;

import com.example.flema.service.AppOrderService;
import com.example.flema.service.ItemService;
import com.example.flema.service.UserService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;

/**
 * 管理者：商品管理・ユーザ管理・統計画面とCSV出力
 */
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

  private final ItemService itemService;
  private final AppOrderService appOrderService;
  private final UserService userService;

  public AdminController(ItemService itemService,
      AppOrderService appOrderService,
      UserService userService) {
    this.itemService = itemService;
    this.appOrderService = appOrderService;
    this.userService = userService;
  }

  /** 商品管理画面 */
  @GetMapping("/items")
  public String manageItems(Model model) {
    model.addAttribute("items", itemService.getAllItems());
    return "admin_items";
  }

  /** 管理者による商品削除 */
  @PostMapping("/items/{id}/delete")
  public String deleteItemByAdmin(@PathVariable("id") Long itemId) {
    itemService.deleteItem(itemId);
    return "redirect:/admin/items?success=deleted";
  }

  /** ユーザ管理画面 */
  @GetMapping("/users")
  public String manageUsers(Model model) {
    model.addAttribute("users", userService.getAllUsers());
    return "admin_users";
  }

  /** ユーザの有効/無効切り替え */
  @PostMapping("/users/{id}/toggle-enabled")
  public String toggleUserEnabled(@PathVariable("id") Long userId) {
    userService.toggleUserEnabled(userId);
    return "redirect:/admin/users?success=toggled";
  }

  /** 統計ダッシュボード */
  @GetMapping("/statistics")
  public String showStatistics(
      @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

      @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

      Model model) {

    if (startDate == null)
      startDate = LocalDate.now().minusMonths(1);
    if (endDate == null)
      endDate = LocalDate.now();

    model.addAttribute("startDate", startDate);
    model.addAttribute("endDate", endDate);
    model.addAttribute("totalSales",
        appOrderService.getTotalSales(startDate, endDate));
    model.addAttribute("orderCountByStatus",
        appOrderService.getOrderCountByStatus(startDate, endDate));

    return "admin_statistics";
  }

  /** 統計CSV出力 */
  @GetMapping("/statistics/csv")
  public void exportStatisticsCsv(
      @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

      @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

      HttpServletResponse response) throws IOException {

    if (startDate == null)
      startDate = LocalDate.now().minusMonths(1);
    if (endDate == null)
      endDate = LocalDate.now();

    response.setContentType("text/csv; charset=UTF-8");
    response.setHeader(
        "Content-Disposition",
        "attachment; filename=\"flea_market_statistics.csv\"");

    try (PrintWriter writer = response.getWriter()) {

      writer.append("統計期間: ")
          .append(startDate.toString())
          .append(" から ")
          .append(endDate.toString())
          .append("\n\n");

      writer.append("総売上: ")
          .append(appOrderService.getTotalSales(startDate, endDate).toString())
          .append("\n\n");

      writer.append("ステータス別注文数\n");

      appOrderService.getOrderCountByStatus(startDate, endDate)
          .forEach((status, count) -> writer.append(status)
              .append(",")
              .append(count.toString())
              .append("\n"));
    }
  }
}
