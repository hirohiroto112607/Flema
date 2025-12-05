package com.example.flema.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.flema.entity.Item;
import com.example.flema.entity.User;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findByNameContainingIgnoreCaseAndStatus(
        String name,
        String status,
        Pageable pageble
    );

    Page<Item> findByCategoryIdAndStatus(
        Long categoryId,
        String status,
        Pageable pageble
    );

    Page<Item> findByNameContainingIgnoreCaseAndCategoryIdAndStatus(
        String name,
        Long categoryId,
        String status,
        Pageable pageable
    );

    Page<Item> findByStatus(
        String status,
        Pageable pageable
    );

    List<Item> findBySeller(
        User seller
    );

}
