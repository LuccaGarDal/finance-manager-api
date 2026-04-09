package com.lucca.finance_manager_api.infra;

import com.lucca.finance_manager_api.dto.ApiResponseDTO;
import com.lucca.finance_manager_api.dto.ErrorResponseDTO;
import com.lucca.finance_manager_api.exceptions.AccountNotFoundException;
import com.lucca.finance_manager_api.exceptions.InsufficientBalanceException;
import com.lucca.finance_manager_api.exceptions.TransactionDoesNotBelongToAccountException;
import com.lucca.finance_manager_api.exceptions.TransactionNotFoundException;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TransactionDoesNotBelongToAccountException.class)
    public ResponseEntity<ApiResponseDTO<ErrorResponseDTO>> handleTransactionDoesNotBelongToAccountException (TransactionDoesNotBelongToAccountException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(ex.getMessage(), 404);

        return ResponseEntity.status(error.status()).body(new ApiResponseDTO<>(false, error));
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<ApiResponseDTO<ErrorResponseDTO>> handleTransactionNotFoundException (TransactionNotFoundException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(ex.getMessage(), 404);

        return ResponseEntity.status(error.status()).body(new ApiResponseDTO<>(false, error));
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ApiResponseDTO<ErrorResponseDTO>> handleInsufficientBalanceException (InsufficientBalanceException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(ex.getMessage(), 400);

        return ResponseEntity.status(error.status()).body(new ApiResponseDTO<>(false, error));
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ApiResponseDTO<ErrorResponseDTO>> handleAccountNotFoundException (AccountNotFoundException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(ex.getMessage(), 404);

        return ResponseEntity.status(error.status()).body(new ApiResponseDTO<>(false, error));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDTO> handleValidation(MethodArgumentNotValidException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(ex.getMessage(), ex.getStatusCode().value());

        return ResponseEntity.badRequest().body(new ApiResponseDTO(false, error));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponseDTO> handleResponseStatusException(ResponseStatusException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(ex.getMessage(), ex.getStatusCode().value());

        return ResponseEntity.status(ex.getStatusCode()).body(new ApiResponseDTO(false, error));
    }
}