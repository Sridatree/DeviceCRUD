package org.interview.devicecrud.exception;

import org.springframework.http.HttpStatus;

public class InvalidDeviceIdException extends RuntimeException {

    private final HttpStatus status;

    public InvalidDeviceIdException(String message, String exception) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
