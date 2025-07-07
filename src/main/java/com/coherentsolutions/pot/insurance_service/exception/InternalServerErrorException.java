package com.coherentsolutions.pot.insurance_service.exception;

public class InternalServerErrorException extends RuntimeException {
    private final Object details;
    public InternalServerErrorException(String message) {
        super(message); this.details = null;
    }
    public InternalServerErrorException(String message, Object details) {
        super(message); this.details = details;
    }
    public Object getDetails() {
        return details;
    }
}
