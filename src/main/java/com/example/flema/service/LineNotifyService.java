package com.example.flema.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class LineNotifyService {
    // APIエンドポイントURL（未設定ならデフォルト値を使う）
    @Value("${line.notify.api.url:https://notify-api.line.me/api/notify}")
    private String lineNotifyApiUrl;
    // HTTPクライアントの参照
    private final RestTemplate restTemplate;
    
    public LineNotifyService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // アクセストークンと本文を受け取り、LINE Notifyへ送信
    public void sendMessage(String accessToken, String message) {
        // リクエストヘッダを構築
        HttpHeaders headers = new HttpHeaders();
        // フォームURLエンコードを指定
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        // Bearerトークンをセット
        headers.setBearerAuth(accessToken);
        // フォームボディを構築
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        // messageキーに本文を格納
        map.add("message", message);
        // ヘッダ＋本文でエンティティを生成
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        // 送信試行（失敗はログに残して握りつぶす）
        try {
            // POSTでAPIへ投げる
            restTemplate.postForEntity(lineNotifyApiUrl, request, String.class);
            // 成功ログを標準出力へ
            System.out.println("LINE Notify message sent successfully.");
        } catch (Exception e) {
            // 失敗時は標準エラーへ出力して処理継続
            System.err.println("Failed to send LINE Notify message: " + e.getMessage());
        }
    }
}