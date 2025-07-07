package com.coherentsolutions.pot.insurance_service.dto.exception;

public record ErrorDetailsDTO(
        String code,
        String message,
        Object details
){ }
