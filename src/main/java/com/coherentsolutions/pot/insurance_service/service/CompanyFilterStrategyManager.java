package com.coherentsolutions.pot.insurance_service.service;

import com.coherentsolutions.pot.insurance_service.model.Company;
import com.coherentsolutions.pot.insurance_service.dto.CompanyFilter;
import com.coherentsolutions.pot.insurance_service.service.strategies.*;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.function.Predicate;

/**
 * Manages and combines all company filter strategies.
 */
@Component
public class CompanyFilterStrategyManager {
    
    private final List<CompanyFilterStrategy> strategies;
    
    public CompanyFilterStrategyManager(
            NameFilterStrategy nameFilterStrategy,
            CountryCodeFilterStrategy countryCodeFilterStrategy,
            StatusFilterStrategy statusFilterStrategy,
            CreatedDateFilterStrategy createdDateFilterStrategy,
            UpdatedDateFilterStrategy updatedDateFilterStrategy) {
        
        this.strategies = List.of(
            nameFilterStrategy,
            countryCodeFilterStrategy,
            statusFilterStrategy,
            createdDateFilterStrategy,
            updatedDateFilterStrategy
        );
    }
    
    /**
     * Creates a combined predicate that applies all filter strategies.
     * 
     * @param filter the filter criteria
     * @return a predicate that tests if a company matches all filter criteria
     */
    public Predicate<Company> createFilterPredicate(CompanyFilter filter) {
        return company -> strategies.stream()
            .allMatch(strategy -> strategy.matches(company, filter));
    }
} 