package com.coherentsolutions.pot.insurance_service.exception;

public class ValidationException extends RuntimeException {
    private final Object details;

    public ValidationException(String message) {
        super(message);
        this.details = null;
    }
    public ValidationException(Object details) {
        this.details = details;
    }
    public ValidationException(String message, Object details) {
        super(message);
        this.details = details;
    }
    public Object getDetails() {
        return details;
    }
}
