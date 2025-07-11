package com.coherentsolutions.pot.insurance_service.service;

import com.coherentsolutions.pot.insurance_service.model.Company;
import com.coherentsolutions.pot.insurance_service.dto.CompanyFilter;

/**
 * Strategy interface for filtering companies.
 * Each concrete strategy implements a specific filtering logic.
 */
@FunctionalInterface
public interface CompanyFilterStrategy {
    
    /**
     * Tests if a company matches the filter criteria.
     * 
     * @param company the company to test
     * @param filter the filter criteria
     * @return true if the company matches, false otherwise
     */
    boolean matches(Company company, CompanyFilter filter);
} 