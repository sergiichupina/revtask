package org.revolut.chupina.task.trx;

public final class TransactionManager {

    public static void manage(Transactional t) {
        try {
            t.entityManager.getTransaction().begin();
            t.transact();
            t.entityManager.getTransaction().commit();
        } catch (Exception e) {
            t.entityManager.getTransaction().rollback();
            throw new RuntimeException(e);
        } finally {
            if(t.entityManager.isOpen()){
                t.entityManager.close();
            }
        }
    }
}
