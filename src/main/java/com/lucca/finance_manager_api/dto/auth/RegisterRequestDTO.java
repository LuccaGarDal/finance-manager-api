package com.lucca.finance_manager_api.dto.auth;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

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

        @NotNull(message = "Date of birth is required")
        @Past
        LocalDate dateOfBirth
) {
}
