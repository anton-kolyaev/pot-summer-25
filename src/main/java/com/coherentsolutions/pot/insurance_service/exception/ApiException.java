package com.coherentsolutions.pot.insurance_service.exception;

public abstract class ApiException extends RuntimeException {
    private final Object details;

    protected ApiException(String message) {
        super(message);
        this.details = null;
    }
    protected ApiException(String message, Object details) {
        super(message);
        this.details = details;
    }
    public Object getDetails() {
        return details;
    }
}
