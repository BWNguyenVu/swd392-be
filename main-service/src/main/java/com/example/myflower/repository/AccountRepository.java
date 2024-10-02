package com.example.myflower.repository;

import com.example.myflower.entity.Account;
import com.example.myflower.entity.enumType.AccountRoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByEmail(String email);

    Account findAccountsByRole(AccountRoleEnum role);
}
