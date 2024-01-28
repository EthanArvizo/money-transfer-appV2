package com.techelevator.tenmo.model;


import java.math.BigDecimal;

public class Account {
    private int accountId;
    private int id;
    private BigDecimal balance;
    public Account() {
    }
    public Account(int accountId, int id, BigDecimal balance) {
        this.accountId = accountId;
        this.id = id;
        this.balance = balance;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
