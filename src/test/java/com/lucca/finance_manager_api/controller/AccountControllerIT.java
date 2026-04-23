package com.lucca.finance_manager_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucca.finance_manager_api.dto.account.AccountRequestDTO;
import com.lucca.finance_manager_api.dto.account.UpdateAccountDTO;
import com.lucca.finance_manager_api.entity.Account;
import com.lucca.finance_manager_api.entity.User;
import com.lucca.finance_manager_api.infra.ratelimit.RateLimitFilter;
import com.lucca.finance_manager_api.repository.AccountRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
@WithMockUser
public class AccountControllerIT {

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

    private Account createAccount () {
        Account account = new Account();
        account.setBalance(BigDecimal.valueOf(50));
        account.setName("Conta");
        return account;
    }

    private User createUser() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setName("Test");
        user.setPassword("123");
        user.setCpf("12345678900");
        user.setDateOfBirth(LocalDate.now());
        return user;
    }

    @Test
    void shouldCreateANewAccount () throws Exception {
        AccountRequestDTO accountRequestDTO = new AccountRequestDTO(
                "Conta"
        );

        User user = new User();
        user.setEmail("test@test.com");
        user.setName("Test");
        user.setPassword("123");
        user.setCpf("12345678900");
        user.setDateOfBirth(LocalDate.now());

        user = userRepository.save(user);

        when(userLoggedProvider.getUser()).thenReturn(user);

        String payload = objectMapper.writeValueAsString(accountRequestDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)).andExpect(status().isCreated())
                .andDo(print());

        List<Account> all = accountRepository.findAll();

        assertEquals(1, all.size());

    }

    @Test
    void shouldListAllAccountSaved () throws Exception {
        User user = createUser();
        userRepository.save(user);

        Account account = createAccount();
        Account account1 = createAccount();
        account.setUser(user);
        account1.setUser(user);

        accountRepository.save(account);
        accountRepository.save(account1);

        when(userLoggedProvider.getUser()).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/accounts"))
                        .andExpect(status().isOk());

        List<Account> all = accountRepository.findAll();

        assertEquals(2, all.size());
    }

    @Test
    void shouldReturnEmptyArrayWhenThereIsNoAccounts() throws Exception {
        User user = createUser();
        userRepository.save(user);

        when(userLoggedProvider.getUser()).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/accounts"))
                .andExpect(status().isOk());

        List<Account> all = accountRepository.findAll();

        assertEquals(0, all.size());
    }

    @Test
    void shouldReturnOneAccount () throws Exception {
        User user = createUser();
        userRepository.save(user);

        Account account = createAccount();
        account.setUser(user);
        accountRepository.save(account);

        when(userLoggedProvider.getUser()).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/accounts/{id}", account.getId()))
                .andExpect(status().isOk());

        Optional<Account> userFound = accountRepository.findByIdAndUserId(account.getId(), user.getId());

        assertTrue(userFound.isPresent());
    }

    @Test
    void shouldThrowNotFoundWhenAccountIsNotFound () throws Exception {
        User user = createUser();
        userRepository.save(user);


        when(userLoggedProvider.getUser()).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/accounts/{id}", 1L))
                .andExpect(status().isNotFound());

        Optional<Account> userFound = accountRepository.findByIdAndUserId(1L, user.getId());

        assertTrue(userFound.isEmpty());
    }

    @Test
    void shouldDeleteAccount() throws Exception {
        User user = createUser();
        userRepository.save(user);

        Account account = createAccount();
        account.setUser(user);
        accountRepository.save(account);

        when(userLoggedProvider.getUser()).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.delete("/accounts/{id}", account.getId()))
                .andExpect(status().isOk());

        List<Account> all = accountRepository.findAll();

        assertEquals(0, all.size());
    }

    @Test
    void shouldThrowNotFoundWhenAccountDeletedIsNotFound () throws Exception {
        User user = createUser();
        userRepository.save(user);

        when(userLoggedProvider.getUser()).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.delete("/accounts/{id}", 1L))
                .andExpect(status().isNotFound());

        Optional<Account> userFound = accountRepository.findByIdAndUserId(1L, user.getId());

        assertTrue(userFound.isEmpty());
    }

    @Test
    void shouldUpdateAccountWithAtributes() throws Exception {
        User user = createUser();
        userRepository.save(user);

        Account account = createAccount();
        account.setUser(user);
        accountRepository.save(account);

        when(userLoggedProvider.getUser()).thenReturn(user);

        UpdateAccountDTO dto = new UpdateAccountDTO(
                "Conta atualizada"
        );

        String payload = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/accounts/{id}", account.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk());

        Optional<Account> userFound = accountRepository.findByIdAndUserId(account.getId(), user.getId());

        assertEquals(userFound.get().getName(), dto.name());

    }

    @Test
    void shouldKeepAtributesIfDtoAtributesAreNull() throws Exception {
        User user = createUser();
        userRepository.save(user);

        Account account = createAccount();
        account.setName("Conta");
        account.setUser(user);
        accountRepository.save(account);

        when(userLoggedProvider.getUser()).thenReturn(user);

        UpdateAccountDTO dto = new UpdateAccountDTO(
                ""
        );

        String payload = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/accounts/{id}", account.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk());

        Optional<Account> userFound = accountRepository.findByIdAndUserId(account.getId(), user.getId());

        assertEquals(userFound.get().getName(), account.getName());

    }

}
