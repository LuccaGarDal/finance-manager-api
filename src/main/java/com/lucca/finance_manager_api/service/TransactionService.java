package com.lucca.finance_manager_api.service;

import com.lucca.finance_manager_api.dto.transaction.PaginatedTransactionResponseDTO;
import com.lucca.finance_manager_api.dto.transaction.TransactionRequestDTO;
import com.lucca.finance_manager_api.dto.transaction.TransactionResponseDTO;
import com.lucca.finance_manager_api.entity.Account;
import com.lucca.finance_manager_api.entity.Transaction;
import com.lucca.finance_manager_api.entity.Type;
import com.lucca.finance_manager_api.entity.User;
import com.lucca.finance_manager_api.mapper.TransactionMapper;
import com.lucca.finance_manager_api.repository.AccountRepository;
import com.lucca.finance_manager_api.repository.TransactionRepository;
import com.lucca.finance_manager_api.security.UserLoggedProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

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
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found")
        );
        if  (!(account.getUser().getId().equals(user.getId()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to create");
        }
        entity.setAccount(account);
        if (entity.getType().equals(Type.EXPENSE)) {
            if (account.getBalance().compareTo(entity.getAmount()) < 0) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "You don't have enough money to this transaction");
            }
            account.setBalance(account.getBalance().subtract(entity.getAmount()));
        }
        if (entity.getType().equals(Type.INCOME)) {
            account.setBalance(account.getBalance().add(entity.getAmount()));
        }
        accountRepository.save(account);
        Transaction save = transactionRepository.save(entity);
        return transactionMapper.toResponse(save);
    }

    public PaginatedTransactionResponseDTO<TransactionResponseDTO> listTransactions (Long id, int page, int limit) {
        User user = provider.getUser();
        Account account = accountRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found")
        );
        if  (!(account.getUser().getId().equals(user.getId()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to list");
        }
        Pageable pageable = PageRequest.of(page, limit);
        Page<Transaction> page1 = transactionRepository.findByAccountId(account.getId(), pageable);
        List<TransactionResponseDTO> data = page1.map(t -> new TransactionResponseDTO(
                t.getType(),
                t.getAmount()
        )).getContent();
        return new PaginatedTransactionResponseDTO<>(data, page, limit, page1.getTotalElements());
    }

    public TransactionResponseDTO getTransaction (Long accountId, Long id) {
        User user = provider.getUser();

        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found")
        );

        if  (!(account.getUser().getId().equals(user.getId()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to access this account");
        }

        Transaction transaction = transactionRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));

        if(!(transaction.getAccount().getId().equals(account.getId()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This transaction does not belong to this account");
        }
        return transactionMapper.toResponse(transaction);
    }

    public void deleteTransaction (Long accountId, Long id) {
        User user = provider.getUser();
        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found")
        );
        if  (!(account.getUser().getId().equals(user.getId()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to access this account");
        }
        Transaction transaction = transactionRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));
        if (!transaction.getAccount().getId().equals(accountId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This transaction does not belong to this account");
        }
        transactionRepository.delete(transaction);
    }
}
