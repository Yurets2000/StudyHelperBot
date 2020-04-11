package com.yube.exceptions;

public class ConfigurationException extends Exception {

    private static final long serialVersionUID = 1247042119962571885L;

    public ConfigurationException() {
    }

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationException(Throwable cause) {
        super(cause);
    }
}
