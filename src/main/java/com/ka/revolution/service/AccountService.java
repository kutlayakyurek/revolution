package com.ka.revolution.service;

import com.ka.revolution.model.com.request.SaveAccountRequest;
import com.ka.revolution.model.persistence.Account;

import java.util.List;

public interface AccountService {

    Account saveAccount(SaveAccountRequest request);

    Account findAccountById(Long id);

    List<Account> getAccounts();

}
