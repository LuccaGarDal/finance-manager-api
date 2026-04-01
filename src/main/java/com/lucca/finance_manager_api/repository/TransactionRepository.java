package com.lucca.finance_manager_api.repository;

import com.lucca.finance_manager_api.entity.Account;
import com.lucca.finance_manager_api.entity.Category;
import com.lucca.finance_manager_api.entity.Transaction;
import com.lucca.finance_manager_api.entity.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountAndTransactionDateLessThanEqualAndAppliedFalse(Account account, LocalDate date);
    @Query("""
    SELECT t FROM Transaction t
    WHERE t.account.id = :accountId
    AND (:type IS NULL OR t.type = :type)
    AND (:category IS NULL OR t.category = :category)
    AND t.transactionDate BETWEEN :start AND :end
    ORDER BY t.transactionDate DESC
    """)
    Page<Transaction> findFilteredTransactions(
            @Param("accountId") Long accountId,
            @Param("type") Type type,
            @Param("category") Category category,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            Pageable pageable
    );
}
