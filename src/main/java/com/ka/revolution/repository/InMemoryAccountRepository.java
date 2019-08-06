package com.ka.revolution.repository;

import com.ka.revolution.model.persistence.Account;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class InMemoryAccountRepository implements AccountRepository {

    private static final ConcurrentHashMap<Long, Account> accountMap = new ConcurrentHashMap<>();

    public boolean transferMoney(
            final Long originatorAccountId,
            final Long destinationAccountId,
            final BigDecimal amount) {

        final Account originatorAccount = findAccountById(originatorAccountId);
        final Account destinationAccount = findAccountById(destinationAccountId);

        if (originatorAccount == null) {
            log.warn("Originator account with id {} could not be found", originatorAccountId);
            return false;
        } else if (destinationAccount == null) {
            log.warn("Destination account with id {} could not be found", destinationAccountId);
            return false;
        }

        final Object formerLock;
        final Object latterLock;

        if (originatorAccountId < destinationAccountId) {
            formerLock = originatorAccountId;
            latterLock = destinationAccountId;
        } else {
            formerLock = destinationAccountId;
            latterLock = originatorAccountId;
        }

        /**
         * If locks are ordered either ascending or descending, it prevents dead lock
         */
        synchronized (formerLock) {
            synchronized (latterLock) {
                if (originatorAccount.getAmount().compareTo(amount) < 0) {
                    throw new IllegalArgumentException(String.format("Not sufficient amount in originator account: %s",
                            originatorAccount.getAmount()));
                }

                originatorAccount.setAmount(originatorAccount.getAmount().subtract(amount));
                destinationAccount.setAmount(destinationAccount.getAmount().add(amount));
                log.debug("Transfer completed successfully between originator account(name: {}, amount: {})"
                                + " and destination account(name: {}, amount: {})",
                        originatorAccount.getFullName(), originatorAccount.getAmount(),
                        destinationAccount.getFullName(), destinationAccount.getAmount());
            }
        }

        return true;
    }

    public Account findAccountById(final Long id) {
        if (id == null) {
            log.debug("Given id is null");
            return null;
        }

        return accountMap.get(id);
    }

    @Override
    public List<Account> getAccounts() {
        return new ArrayList<>(accountMap.values());
    }

    @Override
    public Account saveAccount(final BigDecimal amount, final String fullName) {
        final Long accountId = System.nanoTime();

        final Account newAccount = Account.builder()
                .id(accountId)
                .fullName(fullName)
                .amount(amount == null ? BigDecimal.ZERO : amount)
                .build();

        accountMap.put(accountId, newAccount);

        return newAccount;
    }

}
