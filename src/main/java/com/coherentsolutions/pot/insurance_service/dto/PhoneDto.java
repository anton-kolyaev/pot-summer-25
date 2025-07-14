package com.coherentsolutions.pot.insurance_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PhoneDto {
    private String code;
    private String number;
}
