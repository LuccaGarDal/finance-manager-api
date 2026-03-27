package com.lucca.finance_manager_api.repository;

import com.lucca.finance_manager_api.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByAccountId(Long accountId, Pageable pageable);
}
