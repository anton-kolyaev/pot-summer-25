package com.coherentsolutions.pot.insurance_service.dto;

public record ErrorDetailsDTO(
        int code,
        String message,
        Object details
){ }
