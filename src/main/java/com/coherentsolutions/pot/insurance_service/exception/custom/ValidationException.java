package com.coherentsolutions.pot.insurance_service.exception.custom;

public class ValidationException extends ApiException {
    public ValidationException(String message) {
        super(message);
    }
    public ValidationException(String message, Object details) {
        super(message, details);
    }
}
