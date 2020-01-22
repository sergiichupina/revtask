package org.revolut.chupina.task.service;

import javax.persistence.EntityManager;

public interface LocalEntityManagerFactory {

    EntityManager createEntityManager();
}
