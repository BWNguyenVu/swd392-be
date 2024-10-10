package com.example.myflower.repository;

import com.example.myflower.entity.Account;
import com.example.myflower.entity.FlowerListing;
import com.example.myflower.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    List<OrderDetail> findOrderDetailByOrderSummaryId(Integer orderSummaryId);
    List<OrderDetail> findAllBySeller(Account seller);

}
