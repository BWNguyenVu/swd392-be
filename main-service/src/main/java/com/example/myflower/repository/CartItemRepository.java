package com.example.myflower.repository;

import com.example.myflower.entity.Account;
import com.example.myflower.entity.CartItem;
import com.example.myflower.entity.FlowerListing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    List<CartItem> findAllByUser(Account account);
    CartItem findByUserAndFlower(Account account, FlowerListing flowerListing);
    void deleteAllByUser(Account account);
    Integer countCartItemByFlower_UserAndCreatedAtBetween(Account seller, LocalDateTime startDate, LocalDateTime endDate);
}
