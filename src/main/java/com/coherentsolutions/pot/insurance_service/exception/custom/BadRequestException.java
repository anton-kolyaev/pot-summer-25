package com.coherentsolutions.pot.insurance_service.exception.custom;

public class BadRequestException extends ApiException {
    public BadRequestException(String message) {
        super(message);
    }
    public BadRequestException(String message, Object details) {
        super(message, details);
    }
}
