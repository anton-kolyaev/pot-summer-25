package com.coherentsolutions.pot.insurance_service.service.strategies;

import com.coherentsolutions.pot.insurance_service.model.Company;
import com.coherentsolutions.pot.insurance_service.dto.CompanyFilter;
import com.coherentsolutions.pot.insurance_service.service.CompanyFilterStrategy;
import org.springframework.stereotype.Component;

/**
 * Strategy for filtering companies by update date range.
 */
@Component
public class UpdatedDateFilterStrategy implements CompanyFilterStrategy {
    
    @Override
    public boolean matches(Company company, CompanyFilter filter) {
        if (company.getUpdatedAt() == null) {
            return filter.getUpdatedFrom() == null && filter.getUpdatedTo() == null;
        }
        
        if (filter.getUpdatedFrom() != null && company.getUpdatedAt().isBefore(filter.getUpdatedFrom())) {
            return false;
        }
        if (filter.getUpdatedTo() != null && company.getUpdatedAt().isAfter(filter.getUpdatedTo())) {
            return false;
        }
        return true;
    }
} 