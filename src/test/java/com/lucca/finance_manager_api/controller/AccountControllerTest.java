package com.lucca.finance_manager_api.controller;

import com.lucca.finance_manager_api.config.TokenConfig;
import com.lucca.finance_manager_api.dto.account.AccountResponseDTO;
import com.lucca.finance_manager_api.exceptions.AccountNotFoundException;
import com.lucca.finance_manager_api.infra.ratelimit.RateLimitService;
import com.lucca.finance_manager_api.repository.UserRepository;
import com.lucca.finance_manager_api.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
class AccountControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    TokenConfig tokenConfig;

    @MockitoBean
    RateLimitService rateLimitService;

    @MockitoBean
    UserRepository userRepository;

    @MockitoBean
    AccountService accountService;

    @WithMockUser
    @Test
    void shouldCreateAccountWhenPayloadIsValid() throws Exception {

        String payload = """
                {
                  "name": "Conta",
                  "balance": 500
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/accounts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnBadRequestWhenPayloadIsInvalid() throws Exception {

        String payload = """
                {
                  "name": "Conta"
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/accounts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNotfoundWhenNoOneAccountIsFound() throws Exception {

        when(accountService.getAllAccounts()).thenThrow(new AccountNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/accounts")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void shouldListAllAccounts() throws Exception {
        AccountResponseDTO dto = new AccountResponseDTO(1L, "Conta", BigDecimal.valueOf(500));

        List<AccountResponseDTO> list = List.of(dto);

        when(accountService.getAllAccounts()).thenReturn(list);

        mockMvc.perform(MockMvcRequestBuilders.get("/accounts")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1));

    }

    @Test
    @WithMockUser
    void shouldGetAccountById () throws Exception {
        AccountResponseDTO dto = new AccountResponseDTO(1L, "Conta", BigDecimal.valueOf(500));

        when(accountService.getAccount(1L)).thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.get("/accounts/{id}", 1L)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("Conta"));

    }

    @Test
    void shouldReturnNotFoundWhenAccountIsNotFoundById() throws Exception {

        when(accountService.getAccount(1L)).thenThrow(new AccountNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/accounts/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void shouldDeleteAccountById() throws Exception {

        doNothing().when(accountService).deleteAccount(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/accounts/{id}", 1L))
                .andExpect(jsonPath("$.data").value("Account deleted successfully"))
                .andExpect(status().isOk());

        verify(accountService).deleteAccount(1L);
    }

    @Test
    void shouldReturnNotFoundWhenAccountDeletedDoesNotExists() throws Exception {

        doThrow(new AccountNotFoundException()).when(accountService).deleteAccount(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/accounts/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void shouldUpdateAccountFields () throws Exception {
        AccountResponseDTO response = new AccountResponseDTO(1L, "Conta Atualizada", BigDecimal.valueOf(50));

        String payload = """
                {
                   "name": "Conta Atualizada"
                }
                """;

        when(accountService.updateAccount(eq(1L), any())).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.patch("/accounts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(jsonPath("$.data.name").value("Conta Atualizada"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundWhenUpdateAccountDoesNotExists () throws Exception {

        doThrow(new AccountNotFoundException()).when(accountService).updateAccount(eq(1L), any());

        String payload = """
                {
                   "name": "Conta Atualizada"
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.patch("/accounts/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnMethodNotAllowed() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/accounts/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().isMethodNotAllowed());
    }
}
