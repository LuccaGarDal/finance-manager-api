package com.lucca.finance_manager_api.dto.account;

import java.math.BigDecimal;

public record AccountResponseDTO(
        Long id,

        String name,

        BigDecimal amount

) {
}
