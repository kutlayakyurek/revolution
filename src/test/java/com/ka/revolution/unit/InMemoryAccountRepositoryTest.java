package com.ka.revolution.unit;

import com.ka.revolution.model.persistence.Account;
import com.ka.revolution.repository.AccountRepository;
import com.ka.revolution.repository.InMemoryAccountRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

public class InMemoryAccountRepositoryTest {

    private AccountRepository accountRepository;
    private Account firstAccount;
    private Account secondAccount;
    private Account thirdAccount;

    @Before
    public void beforeTests() {
        accountRepository = new InMemoryAccountRepository();
        firstAccount = accountRepository.saveAccount(new BigDecimal(100000), "Iron Man");
        secondAccount = accountRepository.saveAccount(new BigDecimal(150000), "Captain America");
        thirdAccount = accountRepository.saveAccount(new BigDecimal(200000), "Ant Man");
    }

    @Test
    public void whenSaveAccount_thenSuccess() {
        Assert.assertEquals(BigDecimal.valueOf(100000), firstAccount.getAmount());
        Assert.assertEquals("Iron Man", firstAccount.getFullName());
        Assert.assertNotNull(firstAccount.getId());
    }

    @Test
    public void whenFindAccountById_thenSuccess() {
        final Account foundAccount = accountRepository.findAccountById(firstAccount.getId());
        Assert.assertEquals(BigDecimal.valueOf(100000), foundAccount.getAmount());
        Assert.assertEquals("Iron Man", foundAccount.getFullName());
        Assert.assertNotNull(foundAccount.getId());
    }

    @Test
    public void whenFindAccountByNotExistingId_thenReturnNull() {
        Assert.assertNull(accountRepository.findAccountById(100L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenNotSufficientAmountOnMoneyTransfer_thenThrowException() {
        accountRepository.transferMoney(secondAccount.getId(), firstAccount.getId(), BigDecimal.valueOf(500000));
    }

    @Test
    public void whenInvalidOriginatorAccountIdOnMoneyTransfer_thenFail() {
        Assert.assertFalse(accountRepository.transferMoney(100L, secondAccount.getId(), BigDecimal.valueOf(500000)));
    }

    @Test
    public void whenInvalidDestinationAccountIdOnMoneyTransfer_thenFail() {
        Assert.assertFalse(accountRepository.transferMoney(firstAccount.getId(), 100l, BigDecimal.valueOf(500000)));
    }

    @Test
    public void whenNullOriginationAccountIdOnMoneyTransfer_thenFail() {
        Assert.assertFalse(accountRepository.transferMoney(null, secondAccount.getId(), BigDecimal.valueOf(500000)));
    }

    @Test
    public void whenNullDestinationAccountIdOnMoneyTransfer_thenFail() {
        Assert.assertFalse(accountRepository.transferMoney(firstAccount.getId(), null, BigDecimal.valueOf(500000)));
    }

    @Test
    public void whenSingleMoneyTransfer_thenAmountsAreCorrect() {
        accountRepository.transferMoney(secondAccount.getId(), firstAccount.getId(), BigDecimal.valueOf(25000));
        Assert.assertEquals(BigDecimal.valueOf(125000), firstAccount.getAmount());
        Assert.assertEquals(BigDecimal.valueOf(125000), secondAccount.getAmount());
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

        Assert.assertEquals(BigDecimal.valueOf(100000), firstAccount.getAmount());
        Assert.assertEquals(BigDecimal.valueOf(100000), secondAccount.getAmount());
        Assert.assertEquals(BigDecimal.valueOf(250000), thirdAccount.getAmount());
    }

}
