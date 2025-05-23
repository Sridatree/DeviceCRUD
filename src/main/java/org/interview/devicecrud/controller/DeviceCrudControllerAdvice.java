package org.interview.devicecrud.controller;

import org.interview.devicecrud.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class DeviceCrudControllerAdvice {

    private static final Logger logger = LoggerFactory.getLogger(DeviceCrudControllerAdvice.class);

    private ResponseEntity<Object> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(DuplicateDeviceException.class)
    public ResponseEntity<Object> handleDuplicateDeviceException(DuplicateDeviceException ex) {
        logger.warn("Duplicate device creation attempt: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(InvalidDeviceCreationException.class)
    public ResponseEntity<Object> handleInvalidDeviceCreation(InvalidDeviceCreationException ex) {
        logger.warn("Invalid device creation request: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(InvalidDeviceUpdateException.class)
    public ResponseEntity<Object> handleInvalidDeviceCreation(InvalidDeviceUpdateException ex) {
        logger.warn("Invalid device update request: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(InvalidDeviceIdException.class)
    public ResponseEntity<Object> handleInvalidDeviceCreation(InvalidDeviceIdException ex) {
        logger.warn("Invalid device id passed: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(InvalidDeletionException.class)
    public ResponseEntity<Object> handleInvalidDeviceCreation(InvalidDeletionException ex) {
        logger.warn("Invalid deletion request: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }


    @ExceptionHandler(MongoDBException.class)
    public ResponseEntity<Object> handleMongoDBException(MongoDBException ex) {
        logger.error("MongoDB operation failed: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()+" "+ ex.getException());
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleMongoDBException(IllegalArgumentException ex) {
        logger.error("MongoDB operation failed: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex) {
        logger.error("Unhandled exception occurred: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
    }
}
