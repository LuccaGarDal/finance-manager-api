package com.lucca.finance_manager_api.service;

import com.lucca.finance_manager_api.dto.transaction.PaginatedTransactionResponseDTO;
import com.lucca.finance_manager_api.dto.transaction.TransactionRequestDTO;
import com.lucca.finance_manager_api.dto.transaction.TransactionResponseDTO;
import com.lucca.finance_manager_api.entity.*;
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

import java.time.LocalDate;
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
        if  (!account.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to create");
        }
        entity.setAccount(account);

        if(!entity.getTransactionDate().isAfter(LocalDate.now())) {
            entity.setApplied(true);
            if (entity.getType() == Type.EXPENSE) {
                if (account.getBalance().compareTo(entity.getAmount()) < 0) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "You don't have enough money to this transaction");
                }
                account.setBalance(account.getBalance().subtract(entity.getAmount()));
            }
            if (entity.getType() == Type.INCOME) {
                account.setBalance(account.getBalance().add(entity.getAmount()));
            }
        } else {
            entity.setApplied(false);
        }

        accountRepository.save(account);
        Transaction save = transactionRepository.save(entity);
        return transactionMapper.toResponse(save);
    }

    public PaginatedTransactionResponseDTO<TransactionResponseDTO> listTransactions (Long id,
                                                                                     int page,
                                                                                     int limit,
                                                                                     LocalDate start,
                                                                                     LocalDate end,
                                                                                     Type type,
                                                                                     Category category) {
        User user = provider.getUser();
        Account account = accountRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found")
        );
        if  (!(account.getUser().getId().equals(user.getId()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to list");
        }

        Pageable pageable = PageRequest.of(page, limit);

        LocalDate startDate = (start != null) ? start : LocalDate.of(1900, 1, 1);
        LocalDate endDate = (end != null) ? end : LocalDate.of(3000, 1, 1);

        Page<Transaction> pageResult = transactionRepository.findFilteredTransactions(account.getId(),type, category, startDate, endDate, pageable);

        List<TransactionResponseDTO> data = pageResult.map(t ->
                new TransactionResponseDTO(
                        t.getType(),
                        t.getAmount(),
                        t.getTransactionDate(),
                        t.getCategory()
                )
        ).getContent();

        return new PaginatedTransactionResponseDTO<>(
                data,
                page,
                limit,
                pageResult.getTotalElements()
        );
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
        if (transaction.getType() == Type.EXPENSE) {
            account.setBalance(account.getBalance().add(transaction.getAmount()));
        }
        if (transaction.getType() == Type.INCOME) {
            account.setBalance(account.getBalance().subtract(transaction.getAmount()));
        }
        transactionRepository.delete(transaction);
    }
}
