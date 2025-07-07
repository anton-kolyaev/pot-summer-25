package com.coherentsolutions.pot.insurance_service.dto;

import com.coherentsolutions.pot.insurance_service.model.Phone;
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
    private List<Phone> phones;
    private String email;
    private UUID createdBy;
}

