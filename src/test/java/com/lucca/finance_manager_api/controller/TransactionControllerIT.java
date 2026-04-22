package com.lucca.finance_manager_api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
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
}
