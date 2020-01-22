package org.revolut.chupina.task;

import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.revolut.chupina.task.resource.AccountRESTService;
import org.revolut.chupina.task.service.AccountService;
import org.revolut.chupina.task.service.AccountServiceImpl;
import org.revolut.chupina.task.service.LocalEntityManagerFactory;
import org.revolut.chupina.task.service.LocalEntityManagerImpl;

public class ApplicationConfig extends ResourceConfig {

    private final String PU = "AccountPU";

    public ApplicationConfig() {
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(AccountServiceImpl.class).to(AccountService.class);
                bind(new LocalEntityManagerImpl(PU)).to(LocalEntityManagerFactory.class);
            }
        });
        register(AccountRESTService.class);
    }
}