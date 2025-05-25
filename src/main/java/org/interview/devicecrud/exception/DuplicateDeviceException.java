package org.interview.devicecrud.exception;

import org.springframework.http.HttpStatus;

public class DuplicateDeviceException extends RuntimeException {

    private  HttpStatus status;

    public DuplicateDeviceException(String message) {
        super(message);
        this.status = HttpStatus.CONFLICT; // 409 Conflict
    }


}
