package com.coherentsolutions.pot.insurance_service.exception.custom;

public class GenericApiException extends RuntimeException {
    private final String code;
    private final Object details;

    public GenericApiException(String code, String message) {
        super(message);
        this.code = code;
        this.details = null;
    }
    public GenericApiException(String code, String message, Object details) {
        super(message);
        this.code = code;
        this.details = details;
    }
    public String getCode() {
        return code;
    }
    public Object getDetails() {
        return details;
    }
}
