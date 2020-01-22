package org.revolut.chupina.task.service;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.junit.MockitoJUnitRunner;
import org.revolut.chupina.task.entity.Account;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AccountServiceImplTest {

    private AccountService accountService;

    private final String DEMO_ACCOUNT = "DEMO_ACCOUNT";

    @Before
    public void setUp() {
        LocalEntityManagerFactory entityManagerFactory = new LocalEntityManagerImpl("AccountPU");
        accountService = new AccountServiceImpl(entityManagerFactory);
    }

    @Test
    public void t1_createAccount() {
        Account account = new Account(DEMO_ACCOUNT, "Name");
        String result = accountService.createAccount(account);
        assertEquals(DEMO_ACCOUNT, result);
    }

    @Test
    public void t2_getBalance() {
        BigDecimal balance = accountService.getBalance(DEMO_ACCOUNT);
        assertEquals(new BigDecimal(0), balance);
    }

    @Test
    public void t3_addToAccount() {
        accountService.addToAccount(DEMO_ACCOUNT, new BigDecimal(1000000));
        BigDecimal balance = accountService.getBalance(DEMO_ACCOUNT);
        assertEquals(new BigDecimal(1000000), balance);
    }

    @Test
    public void t4_deleteAccount() {
        accountService.deleteAccount(DEMO_ACCOUNT);
        assertTrue(true);
    }

    @Test
    public void validateAccount_valid() {
        assertEquals(true, accountService.validateAccount("1234567890"));
    }

    @Test
    public void validateAccount_invalid() {
        assertEquals(false, accountService.validateAccount("DUMMY"));
    }

    @Test
    public void transferToAccount_success() {
        accountService.addToAccount("1234567890", new BigDecimal(200));
        accountService.transferToAccount("1234567890", "9087654321", new BigDecimal(100));
        assertTrue(true);
    }

    @Test
    public void transferToAccount_invalidBalance() {
        try {
            accountService.transferToAccount("1234567890", "9087654321", new BigDecimal(10000));
        }catch (RuntimeException e){
            assertThat(e.getMessage(), containsString("Invalid balance on the account: 1234567890"));
        }
    }
}