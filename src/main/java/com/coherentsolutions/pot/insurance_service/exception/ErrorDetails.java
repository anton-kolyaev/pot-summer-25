package com.coherentsolutions.pot.insurance_service.exception;

public record ErrorDetails(
        int code,
        String message,
        Object details
){ }
