package org.revolut.chupina.task.service;

import org.revolut.chupina.task.entity.Account;

import java.math.BigDecimal;

public interface AccountService {

    String createAccount(Account account);

    BigDecimal getBalance(String accountNumber);

    Boolean validateAccount(String accountNumber);

    void deleteAccount(String accountNumber);

    void addToAccount(String accountNumber, BigDecimal amount);

    void transferToAccount(String accountFrom, String accountTo, BigDecimal amount);
}
