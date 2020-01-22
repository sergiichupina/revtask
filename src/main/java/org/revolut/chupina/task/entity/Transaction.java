package org.revolut.chupina.task.entity;

import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

@XmlRootElement
public class Transaction {

    private String accountFrom;

    private String accountTo;

    private BigDecimal amount;

    public Transaction() {
    }

    public Transaction(String accountFrom, String accountTo, BigDecimal amount) {
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.amount = amount;
    }

    public String getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(String accountFrom) {
        this.accountFrom = accountFrom;
    }

    public String getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(String accountTo) {
        this.accountTo = accountTo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "accountFrom='" + accountFrom + '\'' +
                ", accountTo='" + accountTo + '\'' +
                ", amount=" + amount +
                '}';
    }
}
