package com.ka.revolution.repository;

import com.ka.revolution.model.persistence.Account;

import java.math.BigDecimal;

public interface AccountRepository {

    boolean transferMoney(Long originatorAccountId, Long destinationAccountId, BigDecimal amount);

    Account findAccountById(Long id);

    Account saveAccount(BigDecimal amount, String fullName);

}
