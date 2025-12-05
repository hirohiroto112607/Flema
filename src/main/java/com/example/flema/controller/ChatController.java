package com.example.flema.controller;

import com.example.flema.entity.User;
import com.example.flema.service.ChatService;
import com.example.flema.service.ItemService;
import com.example.flema.service.UserService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/chat")
public class ChatController {

  private final ChatService chatService;
  private final ItemService itemService;
  private final UserService userService;

  public ChatController(ChatService chatService,
      ItemService itemService,
      UserService userService) {
    this.chatService = chatService;
    this.itemService = itemService;
    this.userService = userService;
  }

  /** 商品単位のチャット画面を表示 */
  @GetMapping("/{itemId}")
  public String showChatScreen(@PathVariable("itemId") Long itemId,
      Model model) {

    // 商品を取得（見つからなければ例外）
    model.addAttribute(
        "item",
        itemService.getItemById(itemId)
            .orElseThrow(() -> new RuntimeException("Item not found")));

    // チャット履歴（昇順）を取得
    model.addAttribute(
        "chats",
        chatService.getChatMessagesByItem(itemId));

    // 商品詳細テンプレート内にチャットを埋め込んで表示
    return "item_detail";
  }

  /** チャットの新規メッセージ送信 */
  @PostMapping("/{itemId}")
  public String sendMessage(@PathVariable("itemId") Long itemId,
      @AuthenticationPrincipal UserDetails userDetails,
      @RequestParam("message") String message) {

    // ログインユーザをメールアドレスから特定
    User sender = userService.getUserByEmail(userDetails.getUsername())
        .orElseThrow(() -> new RuntimeException("Sender not found"));

    // サービス層に送信処理を委譲
    chatService.sendMessage(itemId, sender, message);

    // 送信後は同じチャット画面へリダイレクト
    return "redirect:/chat/" + itemId;
  }
}
