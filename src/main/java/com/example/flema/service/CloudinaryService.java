package com.example.flema.service;

// CloudinaryのJava SDKのエントリポイントをimport
import com.cloudinary.Cloudinary;
// アップロード/削除で使うユーティリティをimport
import com.cloudinary.utils.ObjectUtils;

// 設定値を外部から注入するためのアノテーションをimport
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

// Springのファイルアップロード表現をimport
import org.springframework.web.multipart.MultipartFile;
// I/O例外処理のためのimport
import java.io.IOException;
// アップロード結果を受け取るMapをimport
import java.util.Map;

@Service
public class CloudinaryService {
    // Cloudinaryクライアントの参照
    private final Cloudinary cloudinary;

    // 必要な認証情報をコンストラクタインジェクションで受け取る
    public CloudinaryService(
        // クラウド名をapplication.propertiesから注入
        @Value("${cloudinary.cloud-name}") String cloudName,
        // APIキーを注入
        @Value("${cloudinary.api-key}") String apiKey,
        // APIシークレットを注入
        @Value("${cloudinary.api-secret}") String apiSecret) {
        // 渡された資格情報でCloudinaryクライアントを初期化
        cloudinary = new Cloudinary(ObjectUtils.asMap(
        "cloud_name", cloudName,
        "api_key", apiKey,
        "api_secret", apiSecret));
    }

    // 画像をアップロードして公開URLを返す（空ファイルはnull）
    public String uploadFile(MultipartFile file) throws IOException {
    // アップロードなしのケースはnullを返す
        if (file.isEmpty()) {
            return null;
        }
        // バイト配列をそのままアップロード（オプションは既定）
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
        ObjectUtils.emptyMap());
        // 返却Mapから公開URLを取り出して返す
        return uploadResult.get("url").toString();
    }

    // Cloudinary上のリソースを削除（URLからpublic_idを推定）
    public void deleteFile(String publicId) throws IOException {
        // URLを/で分割して末尾のファイル名を取り出す
        String[] parts = publicId.split("/");
        // 配列末尾＝ファイル名部分を取得
        String fileName = parts[parts.length - 1];
        // 拡張子を除いたpublic_idを推定
        String publicIdWithoutExtension = fileName.substring(0, fileName.lastIndexOf('.'));
        // public_idを指定して削除APIを呼び出す
        cloudinary.uploader().destroy(publicIdWithoutExtension, ObjectUtils.emptyMap());
    }
}