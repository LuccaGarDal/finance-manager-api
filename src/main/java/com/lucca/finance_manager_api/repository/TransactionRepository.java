package com.lucca.finance_manager_api.repository;

import com.lucca.finance_manager_api.entity.Account;
import com.lucca.finance_manager_api.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByAccountId(Long accountId, Pageable pageable);
    List<Transaction> findByAccountAndTransactionDateLessThanEqualAndAppliedFalse(Account account, LocalDate date);
    Page<Transaction> findByAccountAndTransactionDateBetween(Account account, LocalDate start, LocalDate end, Pageable pageable);
}
