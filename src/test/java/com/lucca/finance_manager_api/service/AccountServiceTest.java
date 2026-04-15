package com.lucca.finance_manager_api.service;

import com.lucca.finance_manager_api.dto.account.AccountRequestDTO;
import com.lucca.finance_manager_api.dto.account.AccountResponseDTO;
import com.lucca.finance_manager_api.entity.Account;
import com.lucca.finance_manager_api.entity.Transaction;
import com.lucca.finance_manager_api.entity.Type;
import com.lucca.finance_manager_api.entity.User;
import com.lucca.finance_manager_api.mapper.AccountMapper;
import com.lucca.finance_manager_api.repository.AccountRepository;
import com.lucca.finance_manager_api.repository.TransactionRepository;
import com.lucca.finance_manager_api.security.UserLoggedProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @InjectMocks
    AccountService accountService;

    @Mock
    AccountRepository accountRepository;

    @Mock
    AccountMapper accountMapper;

    @Mock
    UserLoggedProvider userLoggedProvider;

    @Mock
    TransactionRepository transactionRepository;

    @Test
    void shouldCreateANewAccount () {
        AccountRequestDTO dto = new AccountRequestDTO(
                "Conta teste",
                BigDecimal.valueOf(5000)
        );

        Account account = new Account();
        account.setName("Conta Teste");
        account.setBalance(BigDecimal.valueOf(100));

        User user = new User();
        user.setId(1L);

        Account saved = new Account();
        saved.setName("Conta teste");
        saved.setBalance(BigDecimal.valueOf(5000));
        saved.setUser(user);

        AccountResponseDTO response = new AccountResponseDTO(
                1L,
                "Conta teste",
                BigDecimal.valueOf(5000)
        );

        when(accountMapper.toEntity(dto)).thenReturn(account);
        when(userLoggedProvider.getUser()).thenReturn(user);
        when(accountRepository.save(account)).thenReturn(saved);
        when(accountMapper.toResponse(saved)).thenReturn(response);

        AccountResponseDTO result = accountService.createAccount(dto);

        assertEquals(response, result);
        assertEquals(user, account.getUser());

        verify(accountMapper).toEntity(dto);
        verify(userLoggedProvider).getUser();
        verify(accountRepository).save(account);
        verify(accountMapper).toResponse(saved);
    }

    @Test
    void shouldListAllAccounts () {
        User user = new User();
        user.setId(1L);

        Account account = new Account();
        account.setName("Conta Teste");
        account.setBalance(BigDecimal.valueOf(100));
        account.setUser(user);

        List<Account> accounts = List.of(account);

        Transaction transaction = new Transaction();
        transaction.setType(Type.INCOME);
        transaction.setAmount(BigDecimal.valueOf(50));
        transaction.setApplied(false);

        List<Transaction> transactions = List.of(transaction);

        when(userLoggedProvider.getUser()).thenReturn(user);
        when(accountRepository.findByUserId(user.getId())).thenReturn(accounts);

        when(transactionRepository
                .findByAccountAndTransactionDateLessThanEqualAndAppliedFalse(
                        eq(account),
                        any()
                )).thenReturn(transactions);

        when(accountMapper.toResponse(account)).thenReturn(new AccountResponseDTO(
                1L,"Conta",BigDecimal.valueOf(1000)));

        List<AccountResponseDTO> result = accountService.getAllAccounts();

        assertEquals(1, result.size());
        assertTrue(transaction.getApplied());

        verify(accountRepository).findByUserId(1L);
        verify(accountMapper).toResponse(account);
        verify(accountRepository).save(account);
        verify(transactionRepository).saveAll(transactions);

    }

    @Test
    void shouldApplyPendingTransactionsCorrectly() {
        User user = new User();
        user.setId(1L);

        Account account = new Account();
        account.setName("Conta Teste");
        account.setBalance(BigDecimal.valueOf(100));
        account.setUser(user);

        List<Account> accounts = List.of(account);

        Transaction income = new Transaction();
        income.setType(Type.INCOME);
        income.setAmount(BigDecimal.valueOf(50));
        income.setApplied(false);

        Transaction expense = new Transaction();
        expense.setType(Type.EXPENSE);
        expense.setAmount(BigDecimal.valueOf(30));
        expense.setApplied(false);

        List<Transaction> transactions = List.of(income, expense);

        when(userLoggedProvider.getUser()).thenReturn(user);
        when(accountRepository.findByUserId(user.getId())).thenReturn(accounts);

        when(transactionRepository
                .findByAccountAndTransactionDateLessThanEqualAndAppliedFalse(
                        any(),
                        any()
                )).thenReturn(transactions);

        when(accountMapper.toResponse(account)).thenReturn(new AccountResponseDTO(
                1L,"Conta",BigDecimal.valueOf(1000)));

        List<AccountResponseDTO> result = accountService.getAllAccounts();

        assertEquals(BigDecimal.valueOf(120), account.getBalance());

        assertTrue(income.getApplied());
        assertTrue(expense.getApplied());

        verify(accountRepository).save(account);
        verify(transactionRepository).saveAll(transactions);
    }

    @Test
    void shouldListAccountById () {
        User user = new User();
        user.setId(1L);

        Account account = new Account();
        account.setId(1L);
        account.setName("Conta Teste");
        account.setBalance(BigDecimal.valueOf(100));
        account.setUser(user);

        Transaction income = new Transaction();
        income.setType(Type.INCOME);
        income.setAmount(BigDecimal.valueOf(50));
        income.setApplied(false);

        List<Transaction> transactions = List.of(income);

        when(userLoggedProvider.getUser()).thenReturn(user);
        when(accountRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(account));
        when(transactionRepository.findByAccountAndTransactionDateLessThanEqualAndAppliedFalse(
                any(), any()
        )).thenReturn(transactions);

        AccountResponseDTO result = accountService.getAccount(1L);

        assertEquals(BigDecimal.valueOf(150), account.getBalance());

        verify(accountRepository).save(account);
        verify(transactionRepository).saveAll(transactions);
    }

    @Test
    void shouldUpdateAccountWithAtributesNotNull () {
        User user = new User();
        user.setId(1L);

        Account account = new Account();
        account.setId(1L);
        account.setName("Conta Teste");
        account.setBalance(BigDecimal.valueOf(100));
        account.setUser(user);

        AccountRequestDTO accountRequestDTO = new AccountRequestDTO(
                "Conta",
                BigDecimal.valueOf(1500)
        );

        when(userLoggedProvider.getUser()).thenReturn(user);
        when(accountRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(account));

        accountService.updateAccount(1L, accountRequestDTO);

        assertEquals(BigDecimal.valueOf(1500), account.getBalance());

        verify(accountRepository).save(account);
    }

}
