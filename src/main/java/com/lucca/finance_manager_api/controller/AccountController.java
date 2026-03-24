package com.lucca.finance_manager_api.controller;

import com.lucca.finance_manager_api.dto.ApiResponseDTO;
import com.lucca.finance_manager_api.dto.account.AccountRequestDTO;
import com.lucca.finance_manager_api.dto.account.AccountResponseDTO;
import com.lucca.finance_manager_api.entity.Account;
import com.lucca.finance_manager_api.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    AccountService accountService;

    @PostMapping
    public ResponseEntity<ApiResponseDTO> createAccount (@RequestBody AccountRequestDTO dto) {
        AccountResponseDTO data = accountService.createAccount(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.ok(data));
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO> getAllAccounts () {
        List<AccountResponseDTO> allAccounts = accountService.getAllAccounts();
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.ok(allAccounts));
    }

    @GetMapping
    @RequestMapping("/{id}")
    public ResponseEntity<ApiResponseDTO> getAccount (@PathVariable Long id) {
        AccountResponseDTO account = accountService.getAccount(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.ok(account));
    }

}
