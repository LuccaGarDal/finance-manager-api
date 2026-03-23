package com.lucca.finance_manager_api.service;

import com.lucca.finance_manager_api.dto.AccountRequestDTO;
import com.lucca.finance_manager_api.entity.Account;
import com.lucca.finance_manager_api.mapper.AccountMapper;
import com.lucca.finance_manager_api.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountMapper accountMapper;

    public Account createAccount (AccountRequestDTO dto) {
        return accountRepository.save(accountMapper.toEntity(dto));
    }
}
