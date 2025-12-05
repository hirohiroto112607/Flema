package com.example.flema.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.flema.entity.FavoriteItem;
import com.example.flema.entity.Item;
import com.example.flema.entity.User;

@Repository
public interface FavoriteItemRepository extends JpaRepository<FavoriteItem, Long> {
    
    Optional<FavoriteItem> findByUserAndItem(User user, Item item);

    List<FavoriteItem> findByUser(User user);

    Boolean existsByUserAndItem(User user, Item item);

}