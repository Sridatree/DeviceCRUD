package org.interview.devicecrud.exception;

import org.springframework.http.HttpStatus;

public class InvalidDeletionException extends RuntimeException {

    private final HttpStatus status;

    public InvalidDeletionException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public HttpStatus getStatus() {
        return status;
    }
}