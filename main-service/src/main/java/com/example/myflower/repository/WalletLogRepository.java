package com.example.myflower.repository;

import com.example.myflower.entity.WalletLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletLogRepository extends JpaRepository<WalletLog, Integer> {
}
