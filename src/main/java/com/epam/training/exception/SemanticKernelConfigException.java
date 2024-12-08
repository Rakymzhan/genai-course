package com.epam.training.exception;

public class SemanticKernelConfigException extends RuntimeException {

    private static final String MESSAGE = "Check configuration: %s";

    public SemanticKernelConfigException(String message, Throwable cause) {
        super(String.format(MESSAGE, message), cause);
    }
}
