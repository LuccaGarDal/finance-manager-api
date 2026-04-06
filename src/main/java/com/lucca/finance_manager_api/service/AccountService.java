package com.lucca.finance_manager_api.service;

import com.lucca.finance_manager_api.dto.account.AccountRequestDTO;
import com.lucca.finance_manager_api.dto.account.AccountResponseDTO;
import com.lucca.finance_manager_api.entity.Account;
import com.lucca.finance_manager_api.entity.Transaction;
import com.lucca.finance_manager_api.entity.Type;
import com.lucca.finance_manager_api.entity.User;
import com.lucca.finance_manager_api.mapper.AccountMapper;
import com.lucca.finance_manager_api.repository.AccountRepository;
import com.lucca.finance_manager_api.repository.TransactionRepository;
import com.lucca.finance_manager_api.security.UserLoggedProvider;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
public class AccountService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountMapper accountMapper;

    @Autowired
    UserLoggedProvider userLoggedProvider;

    @Autowired
    TransactionRepository transactionRepository;

    public AccountResponseDTO createAccount (AccountRequestDTO dto) {
        Account account = accountMapper.toEntity(dto);
        User user = userLoggedProvider.getUser();
        account.setUser(user);
        Account save = accountRepository.save(account);
        log.info("Conta bancária {} criado com sucesso", save.getName());
        return accountMapper.toResponse(save);
    }

    public List<AccountResponseDTO> getAllAccounts() {
        List<AccountResponseDTO> listDto = new ArrayList<>();
        User user = userLoggedProvider.getUser();
        List<Account> byUserId = accountRepository.findByUserId(user.getId());
        for (Account account : byUserId) {
            applyPendingTransactions(account);
            listDto.add(accountMapper.toResponse(account));
        }
        return listDto;
    }

    public AccountResponseDTO getAccount (Long id) {
        return accountMapper.toResponse(findAccount(id));
    }

    public void deleteAccount (Long id) {
        User user = userLoggedProvider.getUser();
        Account account = accountRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Account not found"
                ));
        log.info("Conta bancária {} deletada com sucesso", account.getName());
        accountRepository.delete(account);
    }

    public AccountResponseDTO updateAccount (Long id, AccountRequestDTO dto) {
        User user = userLoggedProvider.getUser();
        Account account = accountRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Account not found"
                ));
        if (dto.name() != null) { account.setName(dto.name());}
        if (dto.balance() != null) { account.setBalance(dto.balance());}

        Account save = accountRepository.save(account);
        log.info("Conta bancária {} atualizada com sucesso", account.getName());
        return accountMapper.toResponse(save);
    }

    private void applyPendingTransactions(Account account) {
        List<Transaction> pendingTransactions =
                transactionRepository.findByAccountAndTransactionDateLessThanEqualAndAppliedFalse(
                        account,
                        LocalDate.now()
                );

        for (Transaction transaction : pendingTransactions) {

            if (transaction.getType() == Type.INCOME) {
                account.setBalance(account.getBalance().add(transaction.getAmount()));
            }

            if (transaction.getType() == Type.EXPENSE) {
                account.setBalance(account.getBalance().subtract(transaction.getAmount()));
            }

            transaction.setApplied(true);
        }

        accountRepository.save(account);
        transactionRepository.saveAll(pendingTransactions);
    }

    private Account findAccount(Long id) {
        User user = userLoggedProvider.getUser();

        Account account = accountRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Account not found"
                ));

        applyPendingTransactions(account);

        return account;
    }
}
