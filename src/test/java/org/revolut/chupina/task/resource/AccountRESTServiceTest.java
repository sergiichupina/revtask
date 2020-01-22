package org.revolut.chupina.task.resource;

import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.revolut.chupina.task.ApplicationConfig;

import javax.ws.rs.client.AsyncInvoker;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AccountRESTServiceTest extends JerseyTest {

    private final String DEMO_ACCOUNT = "DEMO_ACCOUNT";

    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        return new ApplicationConfig();
    }

    @Test
    public void T0_serviceAvailable() {
        Response response = target("account/ping")
                .request()
                .get();

        assertThat(response.readEntity(String.class), containsString("Service online"));
    }

    @Test
    public void T1_createAccount() {
        Response response = target("account/create")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json("{\"name\":\"Dummy Name\",\"number\":\"" + DEMO_ACCOUNT + "\"}"));

        assertThat(response.readEntity(String.class), containsString(DEMO_ACCOUNT + " has been created"));
    }

    @Test
    public void T6_transferMoney_async() {
        final AsyncInvoker asyncInvoker = target("account/transfer").request().async();
        List<Future<Response>> futures = new ArrayList<>(5);
        int i = 0;
        while (i < 4) {
            futures.add(asyncInvoker.put(
                    Entity.json("{\"accountFrom\":\"1234567890\",\"accountTo\":\"9087654321\",\"amount\":40}")));
            i++;
        }
        futures.stream().forEach(f -> {
            try {
                Response response = f.get();
                System.out.println(response.readEntity(String.class));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });

        final String accountToJson = target("account/balance/9087654321").request().get(String.class);
        assertThat(accountToJson, containsString("80"));

        final String accountFromJson = target("account/balance/1234567890").request().get(String.class);
        assertThat(accountFromJson, containsString("20"));
    }

    @Test
    public void T6_transferMoney_invalidAccount() throws ExecutionException, InterruptedException {
        final AsyncInvoker asyncInvoker = target("account/transfer").request().async();
        final Future<Response> responseFuture = asyncInvoker.put(
                Entity.json("{\"accountFrom\":\"DUMMY\",\"accountTo\":\"9087654321\",\"amount\":100}"));

        final Response response = responseFuture.get();
        assertThat(response.readEntity(String.class), containsString("Invalid account number: DUMMY"));
    }

    @Test
    public void T6_transferMoney_invalidBalance() throws ExecutionException, InterruptedException {
        final AsyncInvoker asyncInvoker = target("account/transfer").request().async();
        final Future<Response> responseFuture = asyncInvoker.put(
                Entity.json("{\"accountFrom\":\"1234567890\",\"accountTo\":\"9087654321\",\"amount\":1000}"));

        final Response response = responseFuture.get();
        assertThat(response.readEntity(String.class), containsString("Invalid balance on the account: 1234567890"));
    }

    @Test
    public void T2_addMoneyToAccount() {
        Response response = target("account/add/" + DEMO_ACCOUNT)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(new BigDecimal(100), MediaType.APPLICATION_JSON));

        assertThat(response.readEntity(String.class), containsString(DEMO_ACCOUNT + " has been updated to 100"));
    }

    @Test
    public void T3_getBalance() {
        final String json = target("account/balance/" + DEMO_ACCOUNT).request().get(String.class);
        assertThat(json, containsString("100"));
    }

    @Test
    public void T4_deleteAccount() {
        Response response = target("account/delete/" + DEMO_ACCOUNT).request().delete();
        assertThat(response.getStatus(), equalTo(Response.Status.NO_CONTENT.getStatusCode()));
    }
}
