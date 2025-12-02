package com.example.flema.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    List<Review> findBySeller(User seller);

    Optional<Review> findByOrderId(Long orderId);

    List<Review> findByReviewer(User reviewer);

}