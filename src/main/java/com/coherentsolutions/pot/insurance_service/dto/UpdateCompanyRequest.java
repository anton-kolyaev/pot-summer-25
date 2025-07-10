package com.coherentsolutions.pot.insurance_service.dto;

import lombok.Data;
import java.util.List;

@Data
public class UpdateCompanyRequest {
    private String name;
    private String countryCode;
    private List<AddressDto> addresses;
    private List<PhoneDto> phones;
    private String email;
    private String website;
    private String status; // ACTIVE or INACTIVE
} 