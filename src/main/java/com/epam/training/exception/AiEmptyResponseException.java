package com.epam.training.exception;

public class AiEmptyResponseException extends RuntimeException {

    private static final String MESSAGE = "An empty response was returned from Chat GPT.\n" +
            "Please, try to rephrase your request";

    public AiEmptyResponseException() {
        super(MESSAGE);
    }
}
