package com.coherentsolutions.pot.insurance_service.dto;

import lombok.Data;
import java.util.List;

@Data
public class UpdateCompanyRequest {
    private String name;
    private String countryCode;
    private List<AddressDto> addressData;
    private List<PhoneDto> phoneData;
    private String email;
    private String website;
    private String status; // ACTIVE or INACTIVE
} 