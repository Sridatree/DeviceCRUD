package org.interview.devicecrud.exception;

import org.springframework.http.HttpStatus;

public class InvalidDeviceCreationException extends RuntimeException {

    private final HttpStatus status;

    public InvalidDeviceCreationException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
