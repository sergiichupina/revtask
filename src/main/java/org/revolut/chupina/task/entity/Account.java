package org.revolut.chupina.task.entity;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "ACCOUNT")
@XmlRootElement
public class Account implements Serializable {

    @Id
    @Column(name = "NUMBER", nullable = false)
    private String number;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "BALANCE", nullable = false)
    private BigDecimal balance;

    @Version
    private Integer version;

    public Account() {
        this.balance = new BigDecimal(0);
    }

    public Account(String number, String name) {
        this.number = number;
        this.name = name;
        this.balance = new BigDecimal(0);
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(number, account.number) &&
                Objects.equals(name, account.name) &&
                Objects.equals(balance, account.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, name, balance);
    }

    @Override
    public String toString() {
        return "Account{" +
                "number='" + number + '\'' +
                ", name='" + name + '\'' +
                ", balance=" + balance +
                '}';
    }
}
