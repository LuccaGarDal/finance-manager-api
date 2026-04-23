package com.lucca.finance_manager_api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucca.finance_manager_api.dto.transaction.TransactionRequestDTO;
import com.lucca.finance_manager_api.dto.transaction.TransactionResponseDTO;
import com.lucca.finance_manager_api.entity.*;
import com.lucca.finance_manager_api.infra.ratelimit.RateLimitFilter;
import com.lucca.finance_manager_api.repository.AccountRepository;
import com.lucca.finance_manager_api.repository.TransactionRepository;
import com.lucca.finance_manager_api.repository.UserRepository;
import com.lucca.finance_manager_api.security.SecurityFilter;
import com.lucca.finance_manager_api.security.UserLoggedProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
@WithMockUser
public class TransactionControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

    @MockitoBean
    private SecurityFilter securityFilter;

    @MockitoBean
    private RateLimitFilter rateLimitFilter;

    @MockitoBean
    private UserLoggedProvider userLoggedProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private User createUser () {
        User user = new User();
        user.setName("Lucca");
        user.setPassword("12345678");
        user.setCpf("09430111590");
        user.setEmail("lucca@gmail.com");
        user.setDateOfBirth(LocalDate.of(2004, 1, 15));
        return user;
    }

    private Account createAccount (User user) {
        Account account = new Account();
        account.setName("Conta Teste");
        account.setBalance(BigDecimal.valueOf(1000));
        account.setUser(user);
        return account;
    }

    private TransactionRequestDTO createRequestDTO() {
        return new TransactionRequestDTO(
                Type.EXPENSE,
                BigDecimal.valueOf(500),
                LocalDate.now(),
                Category.FOOD
        );
    }

    private Transaction createTransaction () {
        Transaction transaction = new Transaction();
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

    @Test
    void shouldCreateATransaction () throws Exception {
        User user = createUser();
        TransactionRequestDTO requestDTO = createRequestDTO();
        User saved = userRepository.save(user);

        Account account = createAccount(saved);
        Account save = accountRepository.save(account);

        when(userLoggedProvider.getUser()).thenReturn(user);

        String payload = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/accounts/{accountId}/transactions", save.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isCreated());

        List<Transaction> all = transactionRepository.findAll();
        assertNotNull(all);
    }

    @Test
    void shouldReturnBadRequestWhenBalanceIsInsufficient () throws Exception {
        User user = createUser();
        TransactionRequestDTO requestDTO = createRequestDTO();
        User saved = userRepository.save(user);

        Account account = createAccount(saved);
        account.setBalance(BigDecimal.valueOf(0));
        Account save = accountRepository.save(account);

        when(userLoggedProvider.getUser()).thenReturn(user);

        String payload = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/accounts/{accountId}/transactions", save.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnArrayWithAllTransactions () throws Exception {
        User user = createUser();
        User saved = userRepository.save(user);

        Account account = createAccount(saved);
        Account save = accountRepository.save(account);

        TransactionRequestDTO dto = createRequestDTO();

        Transaction transaction1 = createTransaction(dto, account);
        Transaction transaction2 = createTransaction(dto, account);
        Transaction transaction3 = createTransaction(dto, account);
        transactionRepository.save(transaction1);
        transactionRepository.save(transaction2);
        transactionRepository.save(transaction3);

        when(userLoggedProvider.getUser()).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/accounts/{accountId}/transactions", save.getId()))
                .andExpect(status().isOk());

        List<Transaction> all = transactionRepository.findAll();

        assertEquals(3, all.size());
    }

    @Test
    void shouldReturnBadRequestWhenAccountDoesNotExists() throws Exception {
        User user = createUser();
        User saved = userRepository.save(user);

        Account account = createAccount(saved);
        accountRepository.save(account);

        when(userLoggedProvider.getUser()).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/accounts/{accountId}/transactions", 21L))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnTransactionById () throws Exception {
        User user = createUser();
        User saved = userRepository.save(user);

        Account account = createAccount(saved);
        Account save = accountRepository.save(account);

        TransactionRequestDTO dto = createRequestDTO();

        Transaction transaction1 = createTransaction(dto, account);
        Transaction save1 = transactionRepository.save(transaction1);
        when(userLoggedProvider.getUser()).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/accounts/{accountId}/transactions/{id}", save.getId(), save1.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundWhenAccountIsNotFound() throws Exception {
        User user = createUser();
        User saved = userRepository.save(user);

        Account account = createAccount(saved);
        accountRepository.save(account);

        TransactionRequestDTO dto = createRequestDTO();

        Transaction transaction1 = createTransaction(dto, account);
        Transaction save1 = transactionRepository.save(transaction1);
        when(userLoggedProvider.getUser()).thenReturn(user);
        mockMvc.perform(MockMvcRequestBuilders.get("/accounts/{accountId}/transactions/{id}",4L, save1.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenTransactionIsNotFound() throws Exception {
        User user = createUser();
        User saved = userRepository.save(user);

        Account account = createAccount(saved);
        Account save = accountRepository.save(account);

        when(userLoggedProvider.getUser()).thenReturn(user);
        mockMvc.perform(MockMvcRequestBuilders.get("/accounts/{accountId}/transactions/{id}", save.getId(), 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteAccount() throws Exception {
        User user = createUser();
        User saved = userRepository.save(user);

        Account account = createAccount(saved);
        Account save = accountRepository.save(account);

        TransactionRequestDTO dto = createRequestDTO();

        Transaction transaction1 = createTransaction(dto, account);
        Transaction save1 = transactionRepository.save(transaction1);

        when(userLoggedProvider.getUser()).thenReturn(user);
        mockMvc.perform(MockMvcRequestBuilders.delete("/accounts/{accountId}/transactions/{id}", save.getId(), save1.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldThrowNotFoundWhenAccountIsNotFound() throws Exception {
        User user = createUser();
        User saved = userRepository.save(user);

        when(userLoggedProvider.getUser()).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.delete("/accounts/{accountId}/transactions/{id}", 1L, 1L ))
                .andExpect(status().isNotFound());
    }
    @Test
    void shouldThrowNotFoundWhenTransactionIsNotFound() throws Exception {
        User user = createUser();
        User saved = userRepository.save(user);

        Account account = createAccount(saved);
        Account save = accountRepository.save(account);

        when(userLoggedProvider.getUser()).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.delete("/accounts/{accountId}/transactions/{id}", save.getId(), 1L))
                .andExpect(status().isNotFound());
    }

}
