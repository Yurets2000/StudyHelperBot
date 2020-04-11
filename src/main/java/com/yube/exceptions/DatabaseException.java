package com.yube.exceptions;

public class DatabaseException extends Exception {

    private static final long serialVersionUID = -8398617436173566831L;

    public DatabaseException() {
    }

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseException(Throwable cause) {
        super(cause);
    }
}
