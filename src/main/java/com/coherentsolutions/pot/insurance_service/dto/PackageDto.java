package com.coherentsolutions.pot.insurance_service.dto;

import com.coherentsolutions.pot.insurance_service.model.enums.PayrollFrequency;
import com.coherentsolutions.pot.insurance_service.model.enums.Status;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PackageDto {
    private UUID id;
    private String name;
    private PayrollFrequency frequency;
    private UUID companyId;
    private UUID createdBy;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private PayrollFrequency payrollFrequency;
    private Status status;
    private LocalDateTime createdAt;
}

