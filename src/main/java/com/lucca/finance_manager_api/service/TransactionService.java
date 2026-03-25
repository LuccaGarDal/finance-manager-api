package com.lucca.finance_manager_api.service;

import com.lucca.finance_manager_api.dto.transaction.TransactionRequestDTO;
import com.lucca.finance_manager_api.dto.transaction.TransactionResponseDTO;
import com.lucca.finance_manager_api.entity.Account;
import com.lucca.finance_manager_api.entity.Transaction;
import com.lucca.finance_manager_api.entity.User;
import com.lucca.finance_manager_api.mapper.TransactionMapper;
import com.lucca.finance_manager_api.repository.AccountRepository;
import com.lucca.finance_manager_api.repository.TransactionRepository;
import com.lucca.finance_manager_api.security.UserLoggedProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    TransactionMapper transactionMapper;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    UserLoggedProvider provider;

    public TransactionResponseDTO createTransaction (TransactionRequestDTO dto, Long id) {
        User user = provider.getUser();
        Transaction entity = transactionMapper.toEntity(dto);
        Account account = accountRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Account not found")
        );
        if  (!(account.getUser().getId().equals(user.getId()))) {
            throw new RuntimeException("You don't have permission to create");
        }
        entity.setAccount(account);
        Transaction save = transactionRepository.save(entity);
        return transactionMapper.toResponse(save);
    }
}
