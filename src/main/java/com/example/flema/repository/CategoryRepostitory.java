package com.example.flema.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepostitory extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
}