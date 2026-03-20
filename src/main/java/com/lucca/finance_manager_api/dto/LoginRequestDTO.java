package com.lucca.finance_manager_api.dto;

import jakarta.validation.constraints.NotEmpty;

public record LoginRequestDTO(
        @NotEmpty(message = "Email is required")
        String email,

        @NotEmpty (message = "Password is required")
        String password
) {
}
