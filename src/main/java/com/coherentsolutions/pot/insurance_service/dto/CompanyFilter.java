package com.coherentsolutions.pot.insurance_service.dto;

import java.time.Instant;

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

    // Constructor with all parameters
    public CompanyFilter(String name, String countryCode, String status, 
                       Instant createdFrom, Instant createdTo, 
                       Instant updatedFrom, Instant updatedTo) {
        this.name = name;
        this.countryCode = countryCode;
        this.status = status;
        this.createdFrom = createdFrom;
        this.createdTo = createdTo;
        this.updatedFrom = updatedFrom;
        this.updatedTo = updatedTo;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedFrom() {
        return createdFrom;
    }

    public void setCreatedFrom(Instant createdFrom) {
        this.createdFrom = createdFrom;
    }

    public Instant getCreatedTo() {
        return createdTo;
    }

    public void setCreatedTo(Instant createdTo) {
        this.createdTo = createdTo;
    }

    public Instant getUpdatedFrom() {
        return updatedFrom;
    }

    public void setUpdatedFrom(Instant updatedFrom) {
        this.updatedFrom = updatedFrom;
    }

    public Instant getUpdatedTo() {
        return updatedTo;
    }

    public void setUpdatedTo(Instant updatedTo) {
        this.updatedTo = updatedTo;
    }
} 