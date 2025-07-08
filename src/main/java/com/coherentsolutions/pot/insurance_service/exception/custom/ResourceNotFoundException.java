package com.coherentsolutions.pot.insurance_service.exception.custom;

public class ResourceNotFoundException extends ApiException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
    public ResourceNotFoundException(String message, Object details) {
        super(message, details);
    }
}
