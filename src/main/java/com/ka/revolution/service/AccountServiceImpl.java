package com.ka.revolution.service;

import com.ka.revolution.model.com.SaveAccountRequest;
import com.ka.revolution.model.persistence.Account;
import com.ka.revolution.repository.AccountRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

    private AccountRepository accountRepository;

    @Override
    public Account saveAccount(final SaveAccountRequest request) {
        log.debug("Saving new account -> Amount: {}, Full Name: {}");
        final Account savedAccount = accountRepository.saveAccount(request.getAmount(), request.getFullName());
        log.debug(savedAccount.toString());

        return savedAccount;
    }

}
