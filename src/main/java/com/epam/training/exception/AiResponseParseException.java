package com.epam.training.exception;

public class AiResponseParseException extends RuntimeException {

    private static final String MESSAGE = "Unable to parse AI response";

    public AiResponseParseException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
