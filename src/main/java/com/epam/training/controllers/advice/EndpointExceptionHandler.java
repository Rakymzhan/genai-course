package com.epam.training.controllers.advice;

import com.epam.training.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class EndpointExceptionHandler {

    @ExceptionHandler(value = JsonFileNotFoundException.class)
    protected ResponseEntity<String> handleInternalServerError(JsonFileNotFoundException ex) {
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

    @ExceptionHandler(value = SemanticKernelConfigException.class)
    protected ResponseEntity<String> handleSemanticKernelConfigError(SemanticKernelConfigException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler(value = DialApiProxyException.class)
    protected ResponseEntity<String> handleDialApiProxyException(DialApiProxyException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
}
