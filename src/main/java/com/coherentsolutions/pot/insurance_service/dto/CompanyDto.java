package com.coherentsolutions.pot.insurance_service.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.coherentsolutions.pot.insurance_service.enums.CompanyStatus;
import com.coherentsolutions.pot.insurance_service.model.Address;
import com.coherentsolutions.pot.insurance_service.model.Phone;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanyDto {
    private UUID id;
    private String name;
    private String countryCode;
    private List<Address> addressData;
    private List<Phone> phoneData;
    private String email;
    private String website;
    private CompanyStatus status;
    private UUID createdBy;
    private Instant createdAt;
    private UUID updatedBy;
    private Instant updatedAt;

}
