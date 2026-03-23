package com.lucca.finance_manager_api.mapper;

import com.lucca.finance_manager_api.dto.AccountRequestDTO;
import com.lucca.finance_manager_api.entity.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public Account toEntity (AccountRequestDTO dto) {
        Account account = new Account();
        account.setBalance(dto.balance());
        account.setName(dto.name());
        return account;
    }
}
