package com.coherentsolutions.pot.insurance_service.dto;

public record ErrorDetailsDTO(
        String code,
        String message,
        Object details
){ }
