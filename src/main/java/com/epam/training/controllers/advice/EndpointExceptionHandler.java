package com.epam.training.controllers.advice;

import com.epam.training.exception.AiEmptyResponseException;
import com.epam.training.exception.AiResponseParseException;
import com.epam.training.exception.JsonSchemaFileNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class EndpointExceptionHandler {

    @ExceptionHandler(value = JsonSchemaFileNotFoundException.class)
    protected ResponseEntity<String> handleInternalServerError(JsonSchemaFileNotFoundException ex) {
        return ResponseEntity.internalServerError().body(ex.getMessage());
    }

    @ExceptionHandler(value = AiEmptyResponseException.class)
    protected ResponseEntity<String> handleAiEmptyResponse(AiEmptyResponseException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ex.getMessage());
    }

    @ExceptionHandler(value = AiResponseParseException.class)
    protected ResponseEntity<String> handleJsonParsError(AiResponseParseException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ex.getMessage());
    }
}
