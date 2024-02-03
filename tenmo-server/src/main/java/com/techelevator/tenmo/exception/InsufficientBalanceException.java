package com.techelevator.tenmo.exception;

public class InsufficientBalanceException extends DaoException {
    public InsufficientBalanceException() {
        super();
    }

    public InsufficientBalanceException(String message) {
        super(message);
    }

    public InsufficientBalanceException(String message, Exception cause) {
        super(message, cause);
    }
}
