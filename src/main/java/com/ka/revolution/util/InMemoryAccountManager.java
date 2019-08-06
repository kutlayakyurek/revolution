package com.ka.revolution.util;

import java.util.concurrent.ConcurrentHashMap;

import com.ka.revolution.model.Account;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
public class InMemoryAccountManager implements AccountManager {

    private static final InMemoryAccountManager INSTANCE = new InMemoryAccountManager();

    private final ConcurrentHashMap<Long, Account> accountMap;

    private InMemoryAccountManager() {
        accountMap = new ConcurrentHashMap<>();
    }

    /**
     * Transfers amount between origination and destination acquiring shared transaction lock.
     *
     * @param originatorAccountId  Unique id of originator
     * @param destinationAccountId Unique id of destination
     * @param amount               Transfer amount
     * @return Money transfer result
     */
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

    /**
     * Returns account by unique id from in memory store.
     *
     * @param id Unique account id
     * @return Found account
     */
    public Account findAccountById(final Long id) {
        if (id == null) {
            log.debug("Given id is null");
            return null;
        }

        return accountMap.get(id);
    }

    /**
     * Saves new account with customer fullname and initial amount. Id is generated automatically
     *
     * @param amount   Initial money amount
     * @param fullName Customer name and surname
     * @return Generated account
     */
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

    public static InMemoryAccountManager getInstance() {
        return INSTANCE;
    }

}
