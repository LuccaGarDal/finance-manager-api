package com.lucca.finance_manager_api.dto.auth;

import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDate;

public record RegisterRequestDTO(
        @NotEmpty (message = "Name is required")
        String name,

        @NotEmpty (message = "E-mail is required")
        String email,

        @NotEmpty (message = "Password is required")
        String password,

        @NotEmpty (message = "Cpf is required")
        String cpf,

        @NotEmpty (message = "Date of birth is required")
        LocalDate dateOfBirth
) {
}
