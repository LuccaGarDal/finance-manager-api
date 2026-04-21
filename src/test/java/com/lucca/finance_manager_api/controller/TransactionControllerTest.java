package com.lucca.finance_manager_api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucca.finance_manager_api.config.TokenConfig;
import com.lucca.finance_manager_api.dto.transaction.PaginatedTransactionResponseDTO;
import com.lucca.finance_manager_api.dto.transaction.TransactionRequestDTO;
import com.lucca.finance_manager_api.dto.transaction.TransactionResponseDTO;
import com.lucca.finance_manager_api.entity.Category;
import com.lucca.finance_manager_api.entity.Type;
import com.lucca.finance_manager_api.infra.ratelimit.RateLimitService;
import com.lucca.finance_manager_api.repository.UserRepository;
import com.lucca.finance_manager_api.service.AccountService;
import com.lucca.finance_manager_api.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TransactionControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    TokenConfig tokenConfig;

    @MockitoBean
    RateLimitService rateLimitService;

    @MockitoBean
    UserRepository userRepository;

    @MockitoBean
    TransactionService transactionService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void shouldCreateTransaction () throws Exception {
        TransactionRequestDTO dto = new TransactionRequestDTO(
                Type.INCOME,
                BigDecimal.valueOf(100),
                LocalDate.now(),
                Category.FOOD
        );

        TransactionResponseDTO response = new TransactionResponseDTO(
                Type.INCOME,
                BigDecimal.valueOf(100),
                LocalDate.now(),
                Category.FOOD
        );

        when(transactionService.createTransaction(any(), eq(1L))).thenReturn(response);

        mockMvc.perform(post("/accounts/{accountId}/transactions", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.amount").value(100));

    }

    @Test
    void shouldListAllTransactions () throws Exception {
        PaginatedTransactionResponseDTO<TransactionResponseDTO> response =
                new PaginatedTransactionResponseDTO<>(
                        List.of(
                                new TransactionResponseDTO(
                                        Type.INCOME,
                                        BigDecimal.valueOf(500),
                                        LocalDate.now(),
                                        Category.HOUSING
                                )
                        ),
                        0,
                        1,
                        1L
                );

        when(transactionService.listTransactions(
                eq(1L),
                eq(0),
                eq(20),
                isNull(),
                isNull(),
                isNull(),
                isNull()
        )).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/accounts/{accountId}/transactions", 1L)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.data[0].amount").value(BigDecimal.valueOf(500)));
    }
}
