package com.example.myflower.repository;

import com.example.myflower.entity.Account;
import com.example.myflower.entity.Payment;
import com.example.myflower.entity.WalletLog;
import com.example.myflower.entity.enumType.WalletLogStatusEnum;
import com.example.myflower.entity.enumType.WalletLogTypeEnum;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WalletLogRepository extends JpaRepository<WalletLog, Integer> {

    WalletLog findWalletLogByPayment(Payment payment);

    Page<WalletLog> findWalletLogByUserAndStatusInAndTypeInAndIsDeletedAndCreatedAtBetween(
            Account user,
            List<WalletLogStatusEnum> statuses,
            List<WalletLogTypeEnum> types,
            boolean isDeleted,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    );

    @NonNull
    @Query("SELECT wl FROM WalletLog wl WHERE wl.id = :id AND wl.user = :user AND wl.isDeleted = false")
    Optional<WalletLog> findByIdAndUser(Integer id, Account user);

}
