package com.coherentsolutions.pot.insurance_service.exception;

public class BadRequestException extends ApiException {
    public BadRequestException(String message) {
        super(message);
    }
    public BadRequestException(String message, Object details) {
        super(message, details);
    }
}
