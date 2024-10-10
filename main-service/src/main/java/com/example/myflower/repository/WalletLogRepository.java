package com.example.myflower.repository;

import com.example.myflower.entity.Account;
import com.example.myflower.entity.Payment;
import com.example.myflower.entity.WalletLog;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletLogRepository extends JpaRepository<WalletLog, Integer> {

    WalletLog findWalletLogByPayment(Payment payment);

    @Query("SELECT wl FROM WalletLog wl WHERE wl.user = :user AND wl.isDeleted = false")
    List<WalletLog> findWalletLogByUser(Account user);

    @NonNull
    @Query("SELECT wl FROM WalletLog wl WHERE wl.id = :id AND wl.user = :user AND wl.isDeleted = false")
    Optional<WalletLog> findByIdAndUser(Integer id, Account user);

}
