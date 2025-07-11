package com.coherentsolutions.pot.insurance_service.service.strategies;

import com.coherentsolutions.pot.insurance_service.model.Company;
import com.coherentsolutions.pot.insurance_service.dto.CompanyFilter;
import com.coherentsolutions.pot.insurance_service.service.CompanyFilterStrategy;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Strategy for filtering companies by name.
 */
@Component
public class NameFilterStrategy implements CompanyFilterStrategy {
    
    @Override
    public boolean matches(Company company, CompanyFilter filter) {
        if (!StringUtils.hasText(filter.getName())) {
            return true; // No filter applied
        }
        return company.getName() != null && 
               company.getName().toLowerCase().contains(filter.getName().toLowerCase());
    }
} 