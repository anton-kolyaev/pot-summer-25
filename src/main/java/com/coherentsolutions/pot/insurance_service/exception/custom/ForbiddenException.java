package com.coherentsolutions.pot.insurance_service.exception.custom;

public class ForbiddenException extends ApiException {
    public ForbiddenException(String message) {
        super(message);
    }
    public ForbiddenException(String message, Object details) {
        super(message, details);
    }
}
