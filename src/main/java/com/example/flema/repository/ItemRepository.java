package com.example.flema.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    
    Page<Item> finsByNameContainingIgonreCaseAndStatus(
        String name,
        String status,
        Pageable pageble
    );

    Page<item> findByCategoryIdAndStatus(
        Long categoryId,
        String status,
        Pageable pageble
    );

    Page<item> findByNameContainingIgnoreCaseAndCategoryIdAndStatus(
        String name,
        Long categoryId,
        String status,
        Pageable pageable
    );

    Page<item> findByStatus(
        String status,
        Pageable pageable
    );

    List<Item> findBySeller(
        User seller
    );

}