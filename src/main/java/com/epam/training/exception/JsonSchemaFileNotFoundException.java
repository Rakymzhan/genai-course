package com.epam.training.exception;

public class JsonSchemaFileNotFoundException extends RuntimeException {

    private static final String MESSAGE = "File containing JSON schema not found. File name: %s.\n" +
            "Please, contact developers";

    public JsonSchemaFileNotFoundException(String filename, Throwable cause) {
        super(String.format(MESSAGE, filename), cause);
    }
}
