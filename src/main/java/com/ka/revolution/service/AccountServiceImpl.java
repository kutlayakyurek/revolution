package com.ka.revolution.service;

import com.ka.revolution.model.com.request.SaveAccountRequest;
import com.ka.revolution.model.persistence.Account;
import com.ka.revolution.repository.AccountRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

    private AccountRepository accountRepository;

    @Override
    public Account saveAccount(final SaveAccountRequest request) {
        log.debug("Saving new account -> Amount: {}, Full Name: {}", request.getAmount(), request.getFullName());
        final Account savedAccount = accountRepository.saveAccount(request.getAmount(), request.getFullName());

        return savedAccount;
    }

    @Override
    public Account findAccountById(final Long id) {
        log.debug("Finding account -> Id: {}", id);

        return accountRepository.findAccountById(id);
    }

    @Override
    public List<Account> getAccounts() {
        log.debug("Returning all accounts");

        return accountRepository.getAccounts();
    }

}
