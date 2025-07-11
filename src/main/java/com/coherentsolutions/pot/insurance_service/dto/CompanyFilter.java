package com.coherentsolutions.pot.insurance_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;


@Setter
@Getter
@AllArgsConstructor
public class CompanyFilter {
    private String name;
    private String countryCode;
    private String status;
    private Instant createdFrom;
    private Instant createdTo;
    private Instant updatedFrom;
    private Instant updatedTo;

    // Default constructor
    public CompanyFilter() {}

}