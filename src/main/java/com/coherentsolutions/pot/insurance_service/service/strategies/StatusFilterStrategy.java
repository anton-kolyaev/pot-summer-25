package com.coherentsolutions.pot.insurance_service.service.strategies;

import com.coherentsolutions.pot.insurance_service.model.Company;
import com.coherentsolutions.pot.insurance_service.dto.CompanyFilter;
import com.coherentsolutions.pot.insurance_service.model.enums.CompanyStatus;
import com.coherentsolutions.pot.insurance_service.service.CompanyFilterStrategy;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Strategy for filtering companies by status.
 */
@Component
public class StatusFilterStrategy implements CompanyFilterStrategy {
    
    @Override
    public boolean matches(Company company, CompanyFilter filter) {
        if (!StringUtils.hasText(filter.getStatus())) {
            return true; // No filter applied
        }
        try {
            CompanyStatus filterStatus = CompanyStatus.valueOf(filter.getStatus().toUpperCase());
            return company.getStatus() != null && company.getStatus() == filterStatus;
        } catch (IllegalArgumentException e) {
            return false; // Invalid status value
        }
    }
} 