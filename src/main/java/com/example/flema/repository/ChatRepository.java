package com.example.flema.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.flema.entity.Chat;
import com.example.flema.entity.Item;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    List<Chat> findByItemByCreatedAtAsc(Item item);

}