package com.coherentsolutions.pot.insurance_service.exception;

public class ResourceNotFoundException extends RuntimeException {
    private final Object details;

    public ResourceNotFoundException(String message) {
        super(message);
        this.details = null;
    }
    public ResourceNotFoundException(String message, Object details) {
        super(message);
        this.details = details;
    }
    public Object getDetails() {
        return details;
    }
}
