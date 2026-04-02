package com.lucca.finance_manager_api.service;

import com.lucca.finance_manager_api.entity.Account;
import com.lucca.finance_manager_api.entity.User;
import com.lucca.finance_manager_api.repository.AccountRepository;
import com.lucca.finance_manager_api.repository.TransactionRepository;
import com.lucca.finance_manager_api.security.UserLoggedProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DashboardService {
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    UserLoggedProvider userLoggedProvider;

    public BigDecimal getTotalBalance() {
        User user = userLoggedProvider.getUser();
        List<Account> accounts = accountRepository.findByUserId(user.getId());

        return accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

    }
}
