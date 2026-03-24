package com.lucca.finance_manager_api.repository;

import com.lucca.finance_manager_api.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUserId(Long id);
}