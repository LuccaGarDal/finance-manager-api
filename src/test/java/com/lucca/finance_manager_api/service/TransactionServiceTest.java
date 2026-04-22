package com.lucca.finance_manager_api.service;

import com.lucca.finance_manager_api.dto.transaction.PaginatedTransactionResponseDTO;
import com.lucca.finance_manager_api.dto.transaction.TransactionRequestDTO;
import com.lucca.finance_manager_api.dto.transaction.TransactionResponseDTO;
import com.lucca.finance_manager_api.entity.*;
import com.lucca.finance_manager_api.exceptions.AccountNotFoundException;
import com.lucca.finance_manager_api.exceptions.TransactionNotFoundException;
import com.lucca.finance_manager_api.mapper.TransactionMapper;
import com.lucca.finance_manager_api.repository.AccountRepository;
import com.lucca.finance_manager_api.repository.TransactionRepository;
import com.lucca.finance_manager_api.security.UserLoggedProvider;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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

    private User createUser () {
        User user = new User();
        user.setId(1L);
        return user;
    }

    private Account createAccount (User user) {
        Account account = new Account();
        account.setId(1L);
        account.setName("Conta Teste");
        account.setBalance(BigDecimal.valueOf(100));
        account.setUser(user);
        return account;
    }

    private TransactionRequestDTO createRequestDTO() {
        return new TransactionRequestDTO(
                Type.INCOME,
                BigDecimal.valueOf(500),
                LocalDate.now(),
                Category.FOOD
        );
    }

   private Transaction createTransaction () {
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setTransactionDate(LocalDate.now());
        transaction.setCategory(Category.HOUSING);
        transaction.setApplied(true);
        transaction.setAccount(new Account());
        transaction.setAmount(BigDecimal.valueOf(100));
        transaction.setType(Type.EXPENSE);
        return transaction;
    }

    private Transaction createTransaction (TransactionRequestDTO dto, Account account) {
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setTransactionDate(dto.transactionDate());
        transaction.setCategory(dto.category());
        transaction.setApplied(true);
        transaction.setAccount(account);
        transaction.setAmount(dto.amount());
        transaction.setType(dto.type());
        return transaction;
    }

    private TransactionResponseDTO createResponseDTO (Transaction transaction) {
        return new TransactionResponseDTO(
                transaction.getType(),
                transaction.getAmount(),
                transaction.getTransactionDate(),
                transaction.getCategory()
        );
    }

    @Nested
    class saveTransactions {
        @Test
        void shouldCreateATransaction() {
            User user = createUser();
            Account account = createAccount(user);
            TransactionRequestDTO dto = createRequestDTO();
            Transaction transaction = createTransaction(dto, account);
            TransactionResponseDTO dtoResponse = createResponseDTO(transaction);


            when(provider.getUser()).thenReturn(user);
            when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
            when(transactionMapper.toEntity(any())).thenReturn(transaction);
            when(transactionRepository.save(any())).thenReturn(transaction);
            when(transactionMapper.toResponse(any())).thenReturn(dtoResponse);

            TransactionResponseDTO transaction1 = transactionService.createTransaction(dto, 1L);

            assertNotNull(transaction1);

            verify(transactionRepository).save(transaction);

        }

        @Test
        void shouldThrowExceptionWhenAccountIsNotFound () {
            User user = createUser();
            TransactionRequestDTO dto = createRequestDTO();

            when(provider.getUser()).thenReturn(user);
            when(accountRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(AccountNotFoundException.class, () ->
                    transactionService.createTransaction(dto, 1L));
        }
    }

    @Nested
    class listTransactions {

        @Test
        void shouldReturnPaginatedTransactions() {
            User user = createUser();
            Transaction transaction = createTransaction();
            Account account = createAccount(user);

            Page<Transaction> pageResult = new PageImpl<>(List.of(transaction));

            when(provider.getUser()).thenReturn(user);
            when(accountRepository.findById(any())).thenReturn(Optional.of(account));
            when(transactionRepository.findFilteredTransactions(
                    anyLong(),
                    eq(Type.EXPENSE),
                    eq(null),
                    any(),
                    any(),
                    any(Pageable.class)
            )).thenReturn(pageResult);

            transactionService.listTransactions(1L, 0, 10, null, null, Type.EXPENSE, null);

            verify(transactionRepository).findFilteredTransactions(
                    anyLong(),
                    eq(Type.EXPENSE),
                    eq(null),
                    any(),
                    any(),
                    any(Pageable.class)
            );
        }

        @Test
        void shouldThrowExceptionWhenAccountIsNotFound () {
            User user = createUser();
            Account account = createAccount(user);

            when(provider.getUser()).thenReturn(user);
            when(accountRepository.findById(any())).thenReturn(Optional.empty());

            assertThrows(AccountNotFoundException.class, () ->
                    transactionService.listTransactions(1L, 0,10,null,null,null,null));
        }

        @Test
        void shouldReturnEmptyListWhenThereIsNoTransactionsInAccount () {
            User user = createUser();
            Account account = createAccount(user);

            Page<Transaction> pageResult = new PageImpl<>(List.of());

            when(provider.getUser()).thenReturn(user);
            when(accountRepository.findById(any())).thenReturn(Optional.of(account));
            when(transactionRepository.findFilteredTransactions(
                    anyLong(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(Pageable.class)
            )).thenReturn(pageResult);

            PaginatedTransactionResponseDTO<TransactionResponseDTO> result = transactionService
                    .listTransactions(1L, 0, 10, null, null, null, null);

            assertTrue(result.data().isEmpty());
        }
    }

    @Nested
    class getTranscation {

        @Test
        void shouldGetTransaction () {
            User user = createUser();
            Account account = createAccount(user);
            account.setId(1L);
            Transaction transaction = createTransaction();
            transaction.setAccount(account);
            TransactionResponseDTO dto = createResponseDTO(transaction);

            when(provider.getUser()).thenReturn(user);
            when(accountRepository.findById(any())).thenReturn(Optional.of(account));
            when(transactionRepository.findById(any())).thenReturn(Optional.of(transaction));
            when(transactionMapper.toResponse(any())).thenReturn(dto);

            TransactionResponseDTO transaction1 = transactionService.getTransaction(1L, 1L);

            assertEquals(transaction1.amount(), transaction.getAmount());
            assertEquals(transaction1.transactionDate(), transaction.getTransactionDate());

            verify(transactionRepository).findById(1L);

        }

        @Test
        void shouldThrowExceptionWhenAccountIsNotFound() {
            User user = createUser();

            when(provider.getUser()).thenReturn(user);
            when(accountRepository.findById(any())).thenReturn(Optional.empty());

            assertThrows(AccountNotFoundException.class, () ->
                    transactionService.getTransaction(1L, 1L));

        }

        @Test
        void shouldThrowExceptionWhenTransactionIsNotFound() {
            User user = createUser();
            Account account = createAccount(user);

            when(provider.getUser()).thenReturn(user);
            when(accountRepository.findById(any())).thenReturn(Optional.of(account));
            when(transactionRepository.findById(any())).thenReturn(Optional.empty());

            assertThrows(TransactionNotFoundException.class, () ->
                    transactionService.getTransaction(1L, 1L));
        }
    }

    @Nested
    class deleteTransaction {

        @Test
        void shouldDeleteTransactionAndUpdateAccountBalance () {
            User user = createUser();
            Account account = createAccount(user);
            account.setBalance(BigDecimal.valueOf(1000));
            account.setId(1L);

            Transaction transaction = createTransaction();
            transaction.setAccount(account);
            transaction.setType(Type.INCOME);
            transaction.setAmount(BigDecimal.valueOf(500));

            when(provider.getUser()).thenReturn(user);
            when(accountRepository.findById(any())).thenReturn(Optional.of(account));
            when(transactionRepository.findById(any())).thenReturn(Optional.of(transaction));

            transactionService.deleteTransaction(1L, 1L);

            assertEquals(BigDecimal.valueOf(500), account.getBalance());

            verify(transactionRepository).delete(transaction);

        }

        @Test
        void shouldThrowExceptionWhenAccountIsNotFound() {
            User user = createUser();

            when(provider.getUser()).thenReturn(user);
            when(accountRepository.findById(any())).thenReturn(Optional.empty());

            assertThrows(AccountNotFoundException.class, () ->
                    transactionService.deleteTransaction(1L, 1L));

        }

        @Test
        void shouldThrowExceptionWhenTransactionIsNotFound() {
            User user = createUser();
            Account account = createAccount(user);

            when(provider.getUser()).thenReturn(user);
            when(accountRepository.findById(any())).thenReturn(Optional.of(account));
            when(transactionRepository.findById(any())).thenReturn(Optional.empty());

            assertThrows(TransactionNotFoundException.class, () ->
                    transactionService.deleteTransaction(1L, 1L));
        }
    }
}
