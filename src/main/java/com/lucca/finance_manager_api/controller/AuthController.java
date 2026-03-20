package com.lucca.finance_manager_api.controller;

import com.lucca.finance_manager_api.dto.LoginRequestDTO;
import com.lucca.finance_manager_api.dto.RegisterRequestDTO;
import com.lucca.finance_manager_api.entity.User;
import com.lucca.finance_manager_api.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthService userService;

    @PostMapping("/register")
    private ResponseEntity<User> register (@RequestBody RegisterRequestDTO dto) {
        return ResponseEntity.ok().body(userService.register(dto));
    }

    @PostMapping("/login")
    private ResponseEntity<String> login (@RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok().body(userService.login(dto));
    }
}
