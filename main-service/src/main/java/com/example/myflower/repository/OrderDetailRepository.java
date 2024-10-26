package com.example.myflower.repository;

import com.example.myflower.dto.order.responses.CountAndSumOrderResponseDTO;
import com.example.myflower.entity.Account;
import com.example.myflower.entity.FlowerListing;
import com.example.myflower.entity.OrderDetail;
import com.example.myflower.entity.enumType.OrderDetailsStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    List<OrderDetail> findOrderDetailByOrderSummaryId(Integer orderSummaryId);
    @Query("SELECT o FROM OrderDetail o " +
            "JOIN o.orderSummary b " +
            "WHERE o.seller = :seller " +
            "AND (b.buyerPhone LIKE %:search% OR b.buyerName LIKE %:search% OR b.buyerEmail LIKE %:search%) " +
            "AND o.status IN :statuses " +
            "AND o.createdAt BETWEEN :startDate AND :endDate")
    Page<OrderDetail> findAllBySellerAndStatusInAndCreatedAtBetweenAndSearch(
            @Param("seller") Account seller,
            @Param("statuses") List<OrderDetailsStatusEnum> statuses,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("search") String search,
            Pageable pageable);

    @Query("SELECT o FROM OrderDetail o " +
            "JOIN o.orderSummary b " +
            "WHERE o.orderSummary.user = :user " +
            "AND (b.buyerPhone LIKE %:search% OR b.buyerName LIKE %:search% OR b.buyerEmail LIKE %:search%) " +
            "AND o.status IN :statuses " +
            "AND o.createdAt BETWEEN :startDate AND :endDate")
    Page<OrderDetail> findAllByOrderSummary_UserAndStatusInAndCreatedAtBetweenAndSearch(
            @Param("user") Account user,
            @Param("statuses") List<OrderDetailsStatusEnum> statuses,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("search") String search,
            Pageable pageable);

    @Query(value = "SELECT SUM(o.price) as totalPrice, COUNT(o.id) as orders " +
            "FROM OrderDetail o " +
            "WHERE o.seller.id = :sellerId " +
            "AND o.createdAt BETWEEN :startDate AND :endDate")
    List<Object[]> countAndSumPriceBySellerAndCreatedAtBetween(
            @Param("sellerId") Integer sellerId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT od FROM OrderDetail od WHERE od.seller.id = :sellerId " +
            "AND od.createdAt BETWEEN :startDate AND :endDate " +
            "ORDER BY od.createdAt")
    List<OrderDetail> findOrderDetailsBySellerAndDateRange(
            @Param("sellerId") Integer sellerId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
