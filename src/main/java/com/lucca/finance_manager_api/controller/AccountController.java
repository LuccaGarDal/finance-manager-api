package com.lucca.finance_manager_api.controller;

import com.lucca.finance_manager_api.dto.AccountRequestDTO;
import com.lucca.finance_manager_api.entity.Account;
import com.lucca.finance_manager_api.repository.AccountRepository;
import com.lucca.finance_manager_api.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    AccountService accountService;

    @PostMapping
    @RequestMapping("/create")
    public ResponseEntity<Account> createAccount (@RequestBody AccountRequestDTO dto) {
        return ResponseEntity.ok().body(accountService.createAccount(dto));
    }

}
