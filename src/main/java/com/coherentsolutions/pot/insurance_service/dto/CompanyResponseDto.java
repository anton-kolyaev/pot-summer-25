package com.coherentsolutions.pot.insurance_service.dto;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.util.UUID;
import java.util.List;

@Data
@Builder
public class CompanyResponseDto {
    private UUID id;
    private String name;
    private String countryCode;
    private List<AddressDto> addresses;
    private List<PhoneDto> phones;
    private String email;
    private String website;
    private String status; // ACTIVE or INACTIVE
    private UUID whoCreated;
    private Instant createdAt;
    private Instant updatedAt;
} 