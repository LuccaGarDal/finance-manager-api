package com.lucca.finance_manager_api.repository;

import com.lucca.finance_manager_api.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
