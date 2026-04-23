package com.lucca.finance_manager_api.dto.account;

import jakarta.validation.constraints.NotEmpty;

public record AccountRequestDTO(
        @NotEmpty(message = "Name is required")
        String name
) {
}
