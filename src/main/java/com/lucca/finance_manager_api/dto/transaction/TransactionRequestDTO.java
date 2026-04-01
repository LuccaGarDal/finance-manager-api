package com.lucca.finance_manager_api.dto.transaction;

import com.lucca.finance_manager_api.entity.Category;
import com.lucca.finance_manager_api.entity.Type;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionRequestDTO (
        @NotNull(message = "Type is required")
        Type type,

        @NotNull(message = "Amount is required")
        BigDecimal amount,

        @NotNull(message = "Transaction date is required")
        LocalDate transactionDate,

        @NotNull(message = "Category is required")
        Category category
){
}
