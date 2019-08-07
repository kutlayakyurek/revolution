package com.ka.revolution.service;

import com.ka.revolution.model.com.request.SaveAccountRequest;
import com.ka.revolution.model.com.request.TransferMoneyRequest;
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
    public Account findAccountById(final Long accountId) {
        log.debug("Finding account -> Id: {}", accountId);

        return accountRepository.findAccountById(accountId);
    }

    @Override
    public List<Account> getAccounts() {
        log.debug("Returning all accounts");

        return accountRepository.getAccounts();
    }

    @Override
    public void transferMoney(final Long accountId, final TransferMoneyRequest transferMoneyRequest) {
        log.debug("Sending money({}) from origination -> id: {} to destination -> id: {}",
                transferMoneyRequest.getAmount(), accountId, transferMoneyRequest.getDestinationAccountId());

        accountRepository.transferMoney(accountId,
                transferMoneyRequest.getDestinationAccountId(), transferMoneyRequest.getAmount());
    }

}
