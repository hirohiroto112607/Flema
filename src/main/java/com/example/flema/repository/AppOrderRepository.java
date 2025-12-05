package com.example.flema.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.flema.entity.AppOrder;
import com.example.flema.entity.User;

@Repository
public interface AppOrderRepository extends JpaRepository<AppOrder, Long> {

    List<AppOrder> findByBuyer(User buyer);

    List<AppOrder> findByItem_Seller(User seller);
}
