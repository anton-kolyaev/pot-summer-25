package com.coherentsolutions.pot.insurance_service.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class PhoneDto {
    private UUID id;
    private String code;
    private String number;
}

