package com.coherentsolutions.pot.insurance_service.service.strategies;

import com.coherentsolutions.pot.insurance_service.model.Company;
import com.coherentsolutions.pot.insurance_service.dto.CompanyFilter;
import com.coherentsolutions.pot.insurance_service.service.CompanyFilterStrategy;
import org.springframework.stereotype.Component;

/**
 * Strategy for filtering companies by creation date range.
 */
@Component
public class CreatedDateFilterStrategy implements CompanyFilterStrategy {
    
    @Override
    public boolean matches(Company company, CompanyFilter filter) {
        if (company.getCreatedAt() == null) {
            return filter.getCreatedFrom() == null && filter.getCreatedTo() == null;
        }
        
        if (filter.getCreatedFrom() != null && company.getCreatedAt().isBefore(filter.getCreatedFrom())) {
            return false;
        }
        if (filter.getCreatedTo() != null && company.getCreatedAt().isAfter(filter.getCreatedTo())) {
            return false;
        }
        return true;
    }
} 