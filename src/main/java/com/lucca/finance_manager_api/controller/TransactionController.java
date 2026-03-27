package com.lucca.finance_manager_api.controller;

import com.lucca.finance_manager_api.dto.ApiResponseDTO;
import com.lucca.finance_manager_api.dto.transaction.PaginatedTransactionResponseDTO;
import com.lucca.finance_manager_api.dto.transaction.TransactionRequestDTO;
import com.lucca.finance_manager_api.dto.transaction.TransactionResponseDTO;
import com.lucca.finance_manager_api.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @PostMapping("/accounts/{accountId}/transactions")
    public ResponseEntity<ApiResponseDTO> createTransaction (@Valid @RequestBody TransactionRequestDTO dto, @PathVariable Long accountId) {
        TransactionResponseDTO transaction = transactionService.createTransaction(dto, accountId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.ok(transaction));
    }

    @GetMapping("/accounts/{accountId}/transactions")
    public ResponseEntity<ApiResponseDTO> listTransactions (@PathVariable Long accountId,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "20") int limit) {
        PaginatedTransactionResponseDTO<TransactionResponseDTO> pages = transactionService.listTransactions(accountId, page, limit);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.ok(pages));

    }

    @GetMapping("/accounts/{accountId}/transactions/{id}")
    public ResponseEntity<ApiResponseDTO> getTransaction (@PathVariable Long accountId, @PathVariable Long id) {
        TransactionResponseDTO transaction = transactionService.getTransaction(accountId, id);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.ok(transaction));

    }

}
