package com.coherentsolutions.pot.insuranceservice.security.service;

import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class CompanySecurityService {
  public boolean canAccessCompany(UUID companyId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    boolean isAppAdmin = authentication.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals("ROLE_APPLICATION_ADMIN"));
    if (isAppAdmin) {
      return true;
    }

    boolean isCompanyManager = authentication.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals("ROLE_FUNC_COMPANY_MANAGER"));
    if (isCompanyManager) {
      if(authentication.getPrincipal() instanceof Jwt) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String tokenCompanyId = jwt.getClaimAsString("company_id");
        return tokenCompanyId != null && tokenCompanyId.equals(companyId.toString());
      }
    }

    return false;
  }


}
