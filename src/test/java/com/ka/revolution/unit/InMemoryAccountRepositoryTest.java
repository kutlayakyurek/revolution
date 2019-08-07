package com.ka.revolution.unit;

import com.ka.revolution.TestConstants;
import com.ka.revolution.model.persistence.Account;
import com.ka.revolution.repository.AccountRepository;
import com.ka.revolution.repository.InMemoryAccountRepository;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class InMemoryAccountRepositoryTest {

    private AccountRepository accountRepository;
    private Account firstAccount;
    private Account secondAccount;
    private Account thirdAccount;

    @Before
    public void beforeTests() {
        accountRepository = new InMemoryAccountRepository();
        firstAccount = accountRepository.saveAccount(TestConstants.FIRST_ACCOUNT_AMOUNT, TestConstants.FIRST_ACCOUNT_NAME);
        secondAccount = accountRepository.saveAccount(TestConstants.SECOND_ACCOUNT_AMOUNT, TestConstants.SECOND_ACCOUNT_NAME);
        thirdAccount = accountRepository.saveAccount(TestConstants.THIRD_ACCOUNT_AMOUNT, TestConstants.THIRD_ACCOUNT_NAME);
    }

    @Test
    public void whenSaveAccount_thenSuccess() {
        assertEquals(TestConstants.FIRST_ACCOUNT_AMOUNT, firstAccount.getAmount());
        assertEquals(TestConstants.FIRST_ACCOUNT_NAME, firstAccount.getFullName());
        assertNotNull(firstAccount.getId());
    }

    @Test
    public void whenFindAccountById_thenSuccess() {
        final Account foundAccount = accountRepository.findAccountById(firstAccount.getId());
        assertEquals(TestConstants.FIRST_ACCOUNT_AMOUNT, foundAccount.getAmount());
        assertEquals(TestConstants.FIRST_ACCOUNT_NAME, foundAccount.getFullName());
        assertNotNull(foundAccount.getId());
    }

    @Test
    public void whenFindAccountByNotExistingId_thenReturnNull() {
        assertNull(accountRepository.findAccountById(100L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenNotSufficientAmountOnMoneyTransfer_thenThrowException() {
        accountRepository.transferMoney(secondAccount.getId(), firstAccount.getId(), BigDecimal.valueOf(500000));
    }

    @Test
    public void whenInvalidOriginatorAccountIdOnMoneyTransfer_thenFail() {
        assertFalse(accountRepository.transferMoney(100L, secondAccount.getId(), BigDecimal.valueOf(500000)));
    }

    @Test
    public void whenInvalidDestinationAccountIdOnMoneyTransfer_thenFail() {
        assertFalse(accountRepository.transferMoney(firstAccount.getId(), 100l, BigDecimal.valueOf(500000)));
    }

    @Test
    public void whenNullOriginationAccountIdOnMoneyTransfer_thenFail() {
        assertFalse(accountRepository.transferMoney(null, secondAccount.getId(), BigDecimal.valueOf(500000)));
    }

    @Test
    public void whenNullDestinationAccountIdOnMoneyTransfer_thenFail() {
        assertFalse(accountRepository.transferMoney(firstAccount.getId(), null, BigDecimal.valueOf(500000)));
    }

    @Test
    public void whenSingleMoneyTransfer_thenAmountsAreCorrect() {
        accountRepository.transferMoney(secondAccount.getId(), firstAccount.getId(), BigDecimal.valueOf(25000));
        assertEquals(BigDecimal.valueOf(125000), firstAccount.getAmount());
        assertEquals(BigDecimal.valueOf(125000), secondAccount.getAmount());
    }

    @Test
    public void whenConcurrentTransferBetweenTwoAccounts_thenSuccess() throws InterruptedException {
        // Start sending 10000 amount for five times from second account to first one
        final Thread t1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                accountRepository.transferMoney(secondAccount.getId(), firstAccount.getId(), BigDecimal.valueOf(10000));
            }
        });

        // Start sending 10000 amount for five times from first account to second one
        final Thread t2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                accountRepository.transferMoney(firstAccount.getId(), secondAccount.getId(), BigDecimal.valueOf(10000));
            }
        });

        // Start sending 10000 amount for five times from second account to third one
        final Thread t3 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                accountRepository.transferMoney(secondAccount.getId(), thirdAccount.getId(), BigDecimal.valueOf(10000));
            }
        });

        t1.start();
        t2.start();
        t3.start();
        t1.join();
        t2.join();
        t3.join();

        assertEquals(BigDecimal.valueOf(100000), firstAccount.getAmount());
        assertEquals(BigDecimal.valueOf(100000), secondAccount.getAmount());
        assertEquals(BigDecimal.valueOf(250000), thirdAccount.getAmount());
    }

}
