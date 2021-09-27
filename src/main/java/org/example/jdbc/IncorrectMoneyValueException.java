package org.example.jdbc;

public class IncorrectMoneyValueException extends RuntimeException {
    public IncorrectMoneyValueException() {
    }

    public IncorrectMoneyValueException(String message) {
        super(message);
    }

    public IncorrectMoneyValueException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectMoneyValueException(Throwable cause) {
        super(cause);
    }

    protected IncorrectMoneyValueException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
