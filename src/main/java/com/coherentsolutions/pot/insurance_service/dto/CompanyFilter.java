package com.coherentsolutions.pot.insurance_service.dto;

import com.coherentsolutions.pot.insurance_service.enums.CompanyStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;


@Setter
@Getter
@NoArgsConstructor
public class CompanyFilter {
    private String name;
    private String countryCode;
    private CompanyStatus status;
    private Instant createdFrom;
    private Instant createdTo;
    private Instant updatedFrom;
    private Instant updatedTo;
}