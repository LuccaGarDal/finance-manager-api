package com.lucca.finance_manager_api.dto.account;

import jakarta.validation.constraints.NotEmpty;

import java.math.BigDecimal;

public record AccountRequestDTO(
        @NotEmpty(message = "Name is required")
        String name,

        @NotEmpty (message = "Balance is required")
        BigDecimal balance
) {
}
