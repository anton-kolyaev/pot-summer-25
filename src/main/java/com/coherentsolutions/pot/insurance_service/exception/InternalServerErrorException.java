package com.coherentsolutions.pot.insurance_service.exception;

public class InternalServerErrorException extends ApiException {
    public InternalServerErrorException(String message) {
        super(message);
    }
    public InternalServerErrorException(String message, Object details) {
        super(message, details);
    }
}
