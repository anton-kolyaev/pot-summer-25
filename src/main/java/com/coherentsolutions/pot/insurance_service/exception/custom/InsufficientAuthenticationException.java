package com.coherentsolutions.pot.insurance_service.exception.custom;

public class InsufficientAuthenticationException extends ApiException {
    public InsufficientAuthenticationException(String message) {
        super(message);
    }
    public InsufficientAuthenticationException(String message, Object details) {
        super(message, details);
    }
}
