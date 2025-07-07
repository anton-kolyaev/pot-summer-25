package com.coherentsolutions.pot.insurance_service.exception;

public class ForbiddenException extends RuntimeException {
    private final Object details;

    public ForbiddenException(String message) {
        super(message);
        this.details = null;
    }
    public ForbiddenException(Object details) {
        this.details = details;
    }
    public ForbiddenException(String message, Object details) {
        super(message);
        this.details = details;
    }
    public Object getDetails() {
        return details;
    }
}
