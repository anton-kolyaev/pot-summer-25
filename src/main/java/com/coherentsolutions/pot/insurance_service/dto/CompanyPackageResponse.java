package com.coherentsolutions.pot.insurance_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class CompanyPackageResponse {
    private List<PackageDto> packages;
    private int total;
    private int page;
    private int size;
}

