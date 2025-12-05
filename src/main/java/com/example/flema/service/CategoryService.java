package com.example.flema.service;

import com.example.flema.entity.Category;
import com.example.flema.repository.CategoryRepostitory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    // カテゴリリポジトリの参照
    private final CategoryRepostitory categoryRepository;

    // 依存性をコンストラクタで注入
    public CategoryService(CategoryRepostitory categoryRepository) {
        // フィールドへ設定
        this.categoryRepository = categoryRepository;
    }

    // すべてのカテゴリを取得
    public List<Category> getAllCategories() {
        // 全件取得を委譲
        return categoryRepository.findAll();
    }

    // 主キーでカテゴリを取得
    public Optional<Category> getCategoryById(Long id) {
        // Optionalをそのまま返す
        return categoryRepository.findById(id);
    }

    // 名称でカテゴリを取得（名称は一意前提）
    public Optional<Category> getCategoryByName(String name) {
        // 名称検索を委譲
        return categoryRepository.findByName(name);
    }

    // 新規/更新保存
    public Category saveCategory(Category category) {
        // saveに委譲
        return categoryRepository.save(category);
    }

    // 削除
    public void deleteCategory(Long id) {
        // ID指定で削除
        categoryRepository.deleteById(id);
    }
}
