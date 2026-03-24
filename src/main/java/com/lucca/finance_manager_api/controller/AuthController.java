package com.lucca.finance_manager_api.controller;

import com.lucca.finance_manager_api.dto.ApiResponseDTO;
import com.lucca.finance_manager_api.dto.auth.LoginRequestDTO;
import com.lucca.finance_manager_api.dto.auth.LoginResponseDTO;
import com.lucca.finance_manager_api.dto.auth.RegisterRequestDTO;
import com.lucca.finance_manager_api.entity.User;
import com.lucca.finance_manager_api.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/register")
    private ResponseEntity<ApiResponseDTO> register (@RequestBody RegisterRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.ok(authService.register(dto)));
    }

    @PostMapping("/login")
    private ResponseEntity<ApiResponseDTO> login (@RequestBody LoginRequestDTO dto) {
        String token = authService.login(dto);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.ok(new LoginResponseDTO(token)));
    }
}
