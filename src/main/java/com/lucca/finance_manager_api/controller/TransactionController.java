package com.lucca.finance_manager_api.controller;

import com.lucca.finance_manager_api.dto.ApiResponseDTO;
import com.lucca.finance_manager_api.dto.transaction.TransactionRequestDTO;
import com.lucca.finance_manager_api.dto.transaction.TransactionResponseDTO;
import com.lucca.finance_manager_api.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @PostMapping("/accounts/{accountId}/transactions")
    public ResponseEntity<ApiResponseDTO> createTransaction (@RequestBody TransactionRequestDTO dto, @PathVariable Long accountId) {
        TransactionResponseDTO transaction = transactionService.createTransaction(dto, accountId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.ok(transaction));
    }
}
