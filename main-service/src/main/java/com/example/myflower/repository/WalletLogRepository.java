package com.example.myflower.repository;

import com.example.myflower.entity.Account;
import com.example.myflower.entity.Payment;
import com.example.myflower.entity.WalletLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletLogRepository extends JpaRepository<WalletLog, Integer> {
    WalletLog findWalletLogByPayment(Payment payment);
    List<WalletLog> findWalletLogByUser(Account user);

}
