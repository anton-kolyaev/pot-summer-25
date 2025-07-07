package com.coherentsolutions.pot.insurance_service.dto;

import com.coherentsolutions.pot.insurance_service.model.enums.CompanyStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class CreateCompanyResponse {
    private UUID Id;
    private String name;
    private String countryCode;
    private List<AddressDto> addresses;
    private List<PhoneDto> phones;
    private String email;
    private String website;
    private CompanyStatus companyStatus;
    private UUID createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

