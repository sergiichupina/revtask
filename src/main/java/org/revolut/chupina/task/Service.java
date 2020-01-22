package org.revolut.chupina.task;

import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class Service {

    public void run(ResourceConfig resourceConfig, int port) throws Exception {
        URI baseUri = UriBuilder.fromUri("http://localhost/").port(port).build();
        Server server = JettyHttpContainerFactory.createServer(baseUri, resourceConfig);
        try {
            server.start();
            server.join();
        } finally {
            server.destroy();
        }
    }
}
