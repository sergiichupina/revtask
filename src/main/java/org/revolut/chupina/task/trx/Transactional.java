package org.revolut.chupina.task.trx;

import javax.persistence.EntityManager;

public abstract class Transactional {

    EntityManager entityManager;

    public Transactional(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public abstract void transact();
}
