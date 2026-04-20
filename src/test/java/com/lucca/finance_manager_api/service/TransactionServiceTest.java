package com.lucca.finance_manager_api.service;

import com.lucca.finance_manager_api.dto.transaction.TransactionRequestDTO;
import com.lucca.finance_manager_api.dto.transaction.TransactionResponseDTO;
import com.lucca.finance_manager_api.entity.*;
import com.lucca.finance_manager_api.mapper.TransactionMapper;
import com.lucca.finance_manager_api.repository.AccountRepository;
import com.lucca.finance_manager_api.repository.TransactionRepository;
import com.lucca.finance_manager_api.security.UserLoggedProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    TransactionRepository transactionRepository;

    @Mock
    TransactionMapper transactionMapper;

    @Mock
    AccountRepository accountRepository;

    @Mock
    UserLoggedProvider provider;

    @InjectMocks
    TransactionService transactionService;

    @Test
    void shouldCreateATransaction() {
        User user = new User();
        user.setId(1L);

        Account account = new Account();
        account.setId(1L);
        account.setName("Conta Teste");
        account.setBalance(BigDecimal.valueOf(100));
        account.setUser(user);

        TransactionRequestDTO dto = new TransactionRequestDTO(
                Type.INCOME,
                BigDecimal.valueOf(500),
                LocalDate.now(),
                Category.FOOD
        );

        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setTransactionDate(dto.transactionDate());
        transaction.setCategory(dto.category());
        transaction.setApplied(true);
        transaction.setAccount(account);
        transaction.setAmount(dto.amount());
        transaction.setType(dto.type());

        TransactionResponseDTO dtoResponse = new TransactionResponseDTO(
                transaction.getType(),
                transaction.getAmount(),
                transaction.getTransactionDate(),
                transaction.getCategory()
        );


        when(provider.getUser()).thenReturn(user);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(transactionMapper.toEntity(any())).thenReturn(transaction);
        when(transactionRepository.save(any())).thenReturn(transaction);
        when(transactionMapper.toResponse(any())).thenReturn(dtoResponse);

        TransactionResponseDTO transaction1 = transactionService.createTransaction(dto, 1L);

        assertNotNull(transaction1);

        verify(transactionRepository).save(transaction);

    }

}
