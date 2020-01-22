package org.revolut.chupina.task.service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class LocalEntityManagerImpl implements LocalEntityManagerFactory {

    private EntityManagerFactory entityManagerFactory;

    public LocalEntityManagerImpl(String persistentUnit) {
        entityManagerFactory = Persistence.createEntityManagerFactory(persistentUnit);
    }

    @Override
    public EntityManager createEntityManager() {
        return entityManagerFactory.createEntityManager();
    }
}
