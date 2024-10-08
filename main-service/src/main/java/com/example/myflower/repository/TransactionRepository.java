package com.example.myflower.repository;

import com.example.myflower.entity.Account;
import com.example.myflower.entity.Transaction;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    @Query("SELECT t FROM Transaction t WHERE t.user = :user AND t.isDeleted = false")
    List<Transaction> findTransactionByUser(Account user);
    @NonNull
    @Query("SELECT t FROM Transaction t WHERE t.id = :id AND t.isDeleted = false")
    Optional<Transaction> findById(Integer id);

}
