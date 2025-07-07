package com.coherentsolutions.pot.insurance_service.dto;

import com.coherentsolutions.pot.insurance_service.model.enums.CompanyStatus;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CompanyDetailsResponse {
    private UUID id;
    private String name;
    private List<AddressDto> addresses;
    private List<PhoneDto> phones;
    private String email;
    private String website;
    private CompanyStatus status;
}

