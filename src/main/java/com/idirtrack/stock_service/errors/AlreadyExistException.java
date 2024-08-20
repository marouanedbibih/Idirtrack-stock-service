package com.idirtrack.stock_service.errors;

import com.idirtrack.stock_service.utils.ErrorResponse;

public class AlreadyExistException extends Exception {
    private ErrorResponse response;

    public AlreadyExistException(String message) {
        super(message);
    }

    public AlreadyExistException(ErrorResponse response) {
        this.response = response;
    }

    public ErrorResponse getResponse() {
        return response;
    }
}
