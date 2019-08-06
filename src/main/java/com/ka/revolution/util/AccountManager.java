package com.ka.revolution.util;

import com.ka.revolution.model.Account;

import java.math.BigDecimal;

public interface AccountManager {

    boolean transferMoney(Long originatorAccountId, Long destinationAccount, BigDecimal amount);

    Account findAccountById(Long id);

    Account saveAccount(BigDecimal amount, String fullName);

}
