package com.lucca.finance_manager_api.dto.transaction;

import com.lucca.finance_manager_api.entity.Type;

import java.math.BigDecimal;

public record TransactionRequestDTO (
        Type type,
        BigDecimal amount
){
}
