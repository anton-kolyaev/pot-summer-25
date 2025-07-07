package com.coherentsolutions.pot.insurance_service.exception;

public class BadRequestException extends RuntimeException {
    private final Object details;

    public BadRequestException(String message) {
        super(message);
        this.details = null;
    }
    public BadRequestException(Object details) {
        this.details = details;
    }
    public BadRequestException(String message, Object details) {
        super(message);
        this.details = details;
    }
    public Object getDetails() {
        return details;
    }
}
