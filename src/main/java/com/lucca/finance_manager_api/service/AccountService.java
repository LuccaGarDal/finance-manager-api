package com.lucca.finance_manager_api.service;

import com.lucca.finance_manager_api.dto.AccountRequestDTO;
import com.lucca.finance_manager_api.entity.Account;
import com.lucca.finance_manager_api.entity.User;
import com.lucca.finance_manager_api.mapper.AccountMapper;
import com.lucca.finance_manager_api.repository.AccountRepository;
import com.lucca.finance_manager_api.repository.UserRepository;
import com.lucca.finance_manager_api.security.UserLoggedProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountMapper accountMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserLoggedProvider userLoggedProvider;

    public Account createAccount (AccountRequestDTO dto) {
        Account account = accountMapper.toEntity(dto);
        User user = userLoggedProvider.getUser();
        account.setUser(user);
        return accountRepository.save(account);
    }
}
