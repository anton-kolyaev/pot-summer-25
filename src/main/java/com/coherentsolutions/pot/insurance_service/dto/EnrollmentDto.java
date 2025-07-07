package com.coherentsolutions.pot.insurance_service.dto;


import com.coherentsolutions.pot.insurance_service.model.enums.Status;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.UUID;

@Data
public class EnrollmentDto {
    private UUID id;
    private UUID userId;
    private UUID packageId;
    private BigDecimal electionAmount;
    private BigDecimal contributionAmount;
    private LocalDateTime effectiveDate;
    private LocalDateTime endDate;
    private Status status;
    private UserDto user;
    private PackageDto aPackage;

}

