package org.interview.devicecrud.exception;

import org.springframework.http.HttpStatus;

public class InvalidDeviceUpdateException extends RuntimeException {

    private final HttpStatus status;

    public InvalidDeviceUpdateException(String message) {
            super(message);
            this.status = HttpStatus.BAD_REQUEST;
        }

        public HttpStatus getStatus() {
            return status;
        }
    }

