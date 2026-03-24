package com.lucca.finance_manager_api.repository;

import com.lucca.finance_manager_api.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUserId(Long id);
    Optional<Account> findByIdAndUserId (Long id, Long userId);
}