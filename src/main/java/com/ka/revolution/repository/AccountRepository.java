package com.ka.revolution.repository;

import com.ka.revolution.model.persistence.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountRepository {

    boolean transferMoney(Long originatorAccountId, Long destinationAccountId, BigDecimal amount);

    Account findAccountById(Long id);

    List<Account> getAccounts();

    Account saveAccount(BigDecimal amount, String fullName);

}
