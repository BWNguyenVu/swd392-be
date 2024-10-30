package com.example.myflower.repository;

import com.example.myflower.entity.Account;
import com.example.myflower.entity.enumType.AccountRoleEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByEmail(String email);

    List<Account> findAccountsByRole(AccountRoleEnum role);

    @Query("SELECT acc FROM Account acc " +
            "WHERE :roles IS NULL OR acc.role IN :roles " +
            "AND (" +
            "acc.email ILIKE %:search% " +
            "OR acc.name ILIKE %:search% " +
            "OR acc.phone ILIKE %:search%" +
            ")"
    )
    Page<Account> findAccountWithParameters(
            @Param("roles") List<AccountRoleEnum> roles,
            @Param("search") String search,
            Pageable pageable
    );
}
