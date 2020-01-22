package org.revolut.chupina.task.resource;

import org.glassfish.jersey.server.ManagedAsync;
import org.revolut.chupina.task.entity.Account;
import org.revolut.chupina.task.entity.Transaction;
import org.revolut.chupina.task.service.AccountService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

@Path("/account")
public class AccountRESTService {

    @Inject
    private AccountService accountService;

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @GET
    @Path("/ping")
    public Response ping() {
        return Response.ok().entity("Service online").build();
    }

    @GET
    @Path("/balance/{accountNumber}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBalance(@PathParam("accountNumber") String accountNumber) {
        if (accountService.validateAccount(accountNumber)) {
            return Response.ok()
                    .entity(accountService.getBalance(accountNumber))
                    .build();
        }
        return invalidAccountResponse(accountNumber);
    }

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAccount(Account account) {
        String accountNumber = accountService.createAccount(account);
        return Response.status(201).entity(accountNumber + " has been created").build();
    }

    @PUT
    @Path("/add/{accountNumber}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addToAccount(@PathParam("accountNumber") String accountNumber, BigDecimal amount) {
        if (accountService.validateAccount(accountNumber)) {
            accountService.addToAccount(accountNumber, amount);
            return Response.ok()
                    .entity(String.format("%s has been updated to %s", accountNumber, amount.toPlainString()))
                    .build();
        }
        return invalidAccountResponse(accountNumber);
    }

    @PUT
    @Path("/transfer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ManagedAsync
    public void transferMoney(Transaction transaction, @Suspended final AsyncResponse asyncResponse) {

        boolean validAccounts = validateAccount(transaction.getAccountFrom())
                .thenCombineAsync(validateAccount(transaction.getAccountTo()), (s1, s2) -> s1.equals(s2), executorService)
                .handle((result, error) -> {
                    if (error != null) {
                        asyncResponse.resume(error.getMessage());
                    }
                    return result;
                }).join();


        if (validAccounts) {
            TransactionHandler handler = new TransactionHandler(() -> {
                accountService.transferToAccount(
                        transaction.getAccountFrom(),
                        transaction.getAccountTo(),
                        transaction.getAmount().abs());
                return null;
            });

            CompletableFuture.supplyAsync(handler::doAction)
                    .exceptionally(error -> handler.doRepeat())
                    .handle((result, error) -> {
                        if (error != null) {
                            asyncResponse.resume("Transaction incomplete: " + transaction + " " + error.getMessage());
                        }
                        return asyncResponse.resume("Transaction complete");
                    })
                    .join();
        }
    }

    class TransactionHandler {
        public static final int DEFAULT_RETRIES = 3;
        public static final long DEFAULT_WAIT_TIME_IN_MILLI = 100;

        private int countRepeats = 0;

        private Supplier action;

        public TransactionHandler(Supplier<Integer> action) {
            this.action = action;
        }

        public boolean doRepeat() {
            waitUntilNextTry();
            countRepeats++;
            if (countRepeats < DEFAULT_RETRIES) {
                action.get();
            }
            return true;
        }

        public boolean doAction() {
            action.get();
            return true;
        }

        private void waitUntilNextTry() {
            try {
                Thread.sleep(DEFAULT_WAIT_TIME_IN_MILLI);
            } catch (InterruptedException ignored) {
            }
        }
    }

    private CompletableFuture<Boolean> validateAccount(String accountNumber) {
        return CompletableFuture.supplyAsync(() -> {
            if (!accountService.validateAccount(accountNumber)) {
                throw new IllegalArgumentException("Invalid account number: " + accountNumber);
            }
            return true;
        }, executorService);
    }

    @DELETE
    @Path("/delete/{accountNumber}")
    public Response deleteAccount(@PathParam("accountNumber") String accountNumber) {
        if (accountService.validateAccount(accountNumber)) {
            accountService.deleteAccount(accountNumber);
            return Response.status(Response.Status.NO_CONTENT)
                    .entity(String.format("%s deleted successfully", accountNumber))
                    .build();
        }
        return invalidAccountResponse(accountNumber);
    }

    private Response invalidAccountResponse(String accountNumber) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(String.format("Invalid Account Number: %s", accountNumber))
                .build();
    }

}
