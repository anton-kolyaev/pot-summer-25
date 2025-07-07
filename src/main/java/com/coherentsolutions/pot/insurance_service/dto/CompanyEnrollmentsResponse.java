package com.coherentsolutions.pot.insurance_service.dto;

import lombok.Data;
import java.util.List;

@Data
public class CompanyEnrollmentsResponse {
    private List<EnrollmentDto> enrollments;
    private int total;
    private int size;
}

