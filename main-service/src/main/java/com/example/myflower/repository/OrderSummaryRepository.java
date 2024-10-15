package com.example.myflower.repository;

import com.example.myflower.entity.Account;
import com.example.myflower.entity.OrderSummary;
import com.example.myflower.entity.enumType.OrderStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderSummaryRepository extends JpaRepository<OrderSummary, Integer> {
//    Page<OrderSummary> findOrderSummariesByUserAndStatusInAndAndCreatedAtBetween(
//            Account user,
//            List<OrderStatusEnum> statuses,
//            LocalDateTime startDate,
//            LocalDateTime endDate,
//            Pageable pageable);
}
