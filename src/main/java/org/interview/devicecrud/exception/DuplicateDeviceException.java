package org.interview.devicecrud.exception;

import org.springframework.http.HttpStatus;

public class DuplicateDeviceException extends RuntimeException {

    private final HttpStatus status;

    public DuplicateDeviceException(String message) {
        super(message);
        this.status = HttpStatus.CONFLICT; // 409 Conflict
    }


}
