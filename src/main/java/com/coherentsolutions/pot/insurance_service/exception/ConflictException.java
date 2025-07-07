package com.coherentsolutions.pot.insurance_service.exception;

public class ConflictException extends RuntimeException {
    private final Object details;

    public ConflictException(String message) {
        super(message);
        this.details = null;
    }
    public ConflictException(Object details) {
        this.details = details;
    }
    public ConflictException(String message, Object details) {
        super(message);
        this.details = details;
    }
    public Object getDetails() {
        return details;
    }
}
