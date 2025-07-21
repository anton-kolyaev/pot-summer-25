package com.coherentsolutions.pot.insuranceservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

import com.coherentsolutions.pot.insuranceservice.enums.CompanyStatus;


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