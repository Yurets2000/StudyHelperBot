package com.yube.exceptions;

public class ServiceConnectionException extends Exception {

    private static final long serialVersionUID = -6578659907654359242L;

    public ServiceConnectionException() {
    }

    public ServiceConnectionException(String message) {
        super(message);
    }

    public ServiceConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceConnectionException(Throwable cause) {
        super(cause);
    }
}
