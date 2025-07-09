package com.coherentsolutions.pot.insurance_service.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PhoneDto {
    private String code;
    private String number;
}

