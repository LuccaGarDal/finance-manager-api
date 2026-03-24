package com.lucca.finance_manager_api.mapper;

import com.lucca.finance_manager_api.dto.auth.LoginResponseDTO;
import com.lucca.finance_manager_api.dto.auth.RegisterRequestDTO;
import com.lucca.finance_manager_api.dto.auth.RegisterResponseDTO;
import com.lucca.finance_manager_api.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity (RegisterRequestDTO dto) {
        User user = new User();
        user.setName(dto.name());
        user.setCpf(dto.cpf());
        user.setEmail(dto.email());
        user.setPassword(dto.password());
        user.setDateOfBirth(dto.dateOfBirth());

        return user;
    }

    public RegisterResponseDTO toRegisterResponse (User user) {
        return new RegisterResponseDTO(
                user.getName(),
                user.getEmail()
        );
    }

}
