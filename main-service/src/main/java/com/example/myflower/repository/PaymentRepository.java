package com.example.myflower.repository;

import com.example.myflower.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Payment findByPaymentLinkId(String paymentId);
}
