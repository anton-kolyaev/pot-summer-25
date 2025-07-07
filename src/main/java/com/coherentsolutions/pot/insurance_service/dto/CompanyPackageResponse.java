package com.coherentsolutions.pot.insurance_service.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CompanyPackageResponse {
    private List<PackageDto> packages;
    private int total;
    private int page;
    private int size;
}

