package com.coherentsolutions.pot.insurance_service.dto;

import com.coherentsolutions.pot.insurance_service.model.enums.CompanyStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class CompanyDetailsResponse {
    private UUID id;
    private String name;
    private String countryCode;
    private List<AddressDto> addressData;
    private List<PhoneDto> phoneData;
    private String email;
    private String website;
    private CompanyStatus status;
}

