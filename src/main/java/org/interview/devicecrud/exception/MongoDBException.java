package org.interview.devicecrud.exception;

import lombok.Data;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MongoDBException extends RuntimeException {

    private final HttpStatus status;
    private final String exception;

    public MongoDBException(String message, String exception) {
        super(message);
        this.exception = exception;
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }




}