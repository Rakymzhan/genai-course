package com.epam.training.exception;

public class DialApiProxyException extends RuntimeException {

    public DialApiProxyException(String message) {
        super(message);
    }

    public DialApiProxyException(String message, Throwable cause) {
        super(message, cause);
    }
}
