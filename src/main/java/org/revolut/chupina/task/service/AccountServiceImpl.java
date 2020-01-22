package org.revolut.chupina.task.service;

import org.revolut.chupina.task.entity.Account;
import org.revolut.chupina.task.trx.TransactionManager;
import org.revolut.chupina.task.trx.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.math.BigDecimal;

public class AccountServiceImpl implements AccountService {

    @Inject
    private LocalEntityManagerFactory entityManagerFactory;

    public AccountServiceImpl() {
    }

    public AccountServiceImpl(LocalEntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public String createAccount(Account account) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        TransactionManager.manage(new Transactional(entityManager) {
            public void transact() {
                entityManager.persist(account);
            }
        });
        return account.getNumber();
    }

    @Override
    public BigDecimal getBalance(String accountNumber) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Account account = entityManager.find(Account.class, accountNumber);
        return account.getBalance();
    }

    @Override
    public Boolean validateAccount(String accountNumber) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Account account = entityManager.find(Account.class, accountNumber);
        return account != null;
    }

    @Override
    public void deleteAccount(String accountNumber) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Account account = entityManager.find(Account.class, accountNumber);

        TransactionManager.manage(new Transactional(entityManager) {
            public void transact() {
                entityManager.remove(account);
            }
        });
    }

    @Override
    public void addToAccount(String accontNumber, BigDecimal amount) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        Account account = entityManager.find(Account.class, accontNumber);
        account.setBalance(account.getBalance().add(amount.abs()));

        TransactionManager.manage(new Transactional(entityManager) {
            public void transact() {
                entityManager.merge(account);
            }
        });
    }

    @Override
    public void transferToAccount(String from, String to, BigDecimal amount) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        TransactionManager.manage(new Transactional(entityManager) {
            public void transact() {
                Account fromAcc = entityManager.find(Account.class, from, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                BigDecimal subtract = fromAcc.getBalance().subtract(amount);
                if (subtract.doubleValue() < 0) {
                    throw new RuntimeException("Invalid balance on the account: " + fromAcc.getNumber());
                }
                fromAcc.setBalance(subtract);
                entityManager.merge(fromAcc);

                Account toAcc = entityManager.find(Account.class, to, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                toAcc.setBalance(toAcc.getBalance().add(amount));
                entityManager.merge(toAcc);
            }
        });
    }
}
