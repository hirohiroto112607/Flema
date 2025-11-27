package com.example.flema.service;

import com.example.flema.entity.User;
import com.example.flema.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        // フィールドへ設定
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        // 全件取得を委譲
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        // Optionalを返す
        return userRepository.findById(id);
    }

    // メールアドレスでユーザを取得
    public Optional<User> getUserByEmail(String email) {
        // Optionalを返す
        return userRepository.findByEmail(email);
    }

    // 新規/更新保存
    @Transactional
    public User saveUser(User user) {
        // saveに委譲
        return userRepository.save(user);
    }

    // 削除
    @Transactional
    public void deleteUser(Long id) {
        // ID指定で削除
        userRepository.deleteById(id);
    }

    // 有効/無効フラグのトグル
    @Transactional
    public void toggleUserEnabled(Long userId) {
        // IDでユーザを取得（なければ400相当の例外）
        User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));
        // 既存の属性は変更せずenabledだけ反転
        user.setEnabled(!user.isEnabled());
        // 保存して確定
        userRepository.save(user);
    }
}