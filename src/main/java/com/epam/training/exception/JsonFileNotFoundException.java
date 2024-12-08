package com.epam.training.exception;

public class JsonFileNotFoundException extends RuntimeException {

    private static final String MESSAGE = "File %s not found";

    public JsonFileNotFoundException(String filename, Throwable cause) {
        super(String.format(MESSAGE, filename), cause);
    }
}
