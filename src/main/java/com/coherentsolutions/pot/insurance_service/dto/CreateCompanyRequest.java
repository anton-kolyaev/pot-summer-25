package com.coherentsolutions.pot.insurance_service.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class CreateCompanyRequest {
    private String name;
    private String countryCode;
    private List<AddressDto> addresses;
    private List<PhoneDto> phones;
    private String email;
    private String website;
    private UUID createdBy;
}

