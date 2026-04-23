package com.lucca.finance_manager_api.mapper;

import com.lucca.finance_manager_api.dto.account.AccountRequestDTO;
import com.lucca.finance_manager_api.dto.account.AccountResponseDTO;
import com.lucca.finance_manager_api.entity.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public Account toEntity (AccountRequestDTO dto) {
        Account account = new Account();
        account.setName(dto.name());
        return account;
    }

    public AccountResponseDTO toResponse (Account account) {
        return new AccountResponseDTO(
                account.getId(),
                account.getName(),
                account.getBalance()
        );
    }
}
