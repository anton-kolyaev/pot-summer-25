package com.coherentsolutions.pot.insurance_service.exception.custom;

public class ConflictException extends ApiException {
    public ConflictException(String message) {
        super(message);
    }
    public ConflictException(String message, Object details) {
        super(message, details);
    }
}
