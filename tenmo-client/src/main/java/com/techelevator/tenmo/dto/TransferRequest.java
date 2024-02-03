package com.techelevator.tenmo.dto;

import java.math.BigDecimal;

public class TransferRequest {
    private int accountFrom;
    private int accountTo;
    private BigDecimal amount;

    public TransferRequest(int accountFrom, int accountTo, BigDecimal amount) {
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.amount = amount;
    }

    public TransferRequest() {
    }

    public int getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(int accountFrom) {
        this.accountFrom = accountFrom;
    }

    public int getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(int accountTo) {
        this.accountTo = accountTo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
