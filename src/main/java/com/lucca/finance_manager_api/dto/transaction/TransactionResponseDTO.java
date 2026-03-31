package com.lucca.finance_manager_api.dto.transaction;

import com.lucca.finance_manager_api.entity.Type;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionResponseDTO(Type type,
                                     BigDecimal amount,
                                     LocalDate transactionDate) {
}
