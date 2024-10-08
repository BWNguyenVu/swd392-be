package com.example.myflower.repository;

import com.example.myflower.entity.Account;
import com.example.myflower.entity.OrderSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderSummaryRepository extends JpaRepository<OrderSummary, Integer> {
    List<OrderSummary> findOrderSummariesByUser(Account user);
}
