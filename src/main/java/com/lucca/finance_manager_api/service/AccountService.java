package com.lucca.finance_manager_api.service;

import com.lucca.finance_manager_api.dto.account.AccountRequestDTO;
import com.lucca.finance_manager_api.dto.account.AccountResponseDTO;
import com.lucca.finance_manager_api.entity.Account;
import com.lucca.finance_manager_api.entity.User;
import com.lucca.finance_manager_api.mapper.AccountMapper;
import com.lucca.finance_manager_api.repository.AccountRepository;
import com.lucca.finance_manager_api.repository.UserRepository;
import com.lucca.finance_manager_api.security.UserLoggedProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    public AccountResponseDTO createAccount (AccountRequestDTO dto) {
        Account account = accountMapper.toEntity(dto);
        User user = userLoggedProvider.getUser();
        account.setUser(user);
        Account save = accountRepository.save(account);
        return accountMapper.toResponse(save);
    }

    public List<AccountResponseDTO> getAllAccounts() {
        List<AccountResponseDTO> listDto = new ArrayList<>();
        User user = userLoggedProvider.getUser();
        List<Account> byUserId = accountRepository.findByUserId(user.getId());
        for (Account account : byUserId) {
            listDto.add(accountMapper.toResponse(account));
        }

        return listDto;
    }
}
