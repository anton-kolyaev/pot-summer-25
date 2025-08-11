package com.coherentsolutions.pot.insuranceservice.security.service;

import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class UserSecurityService {
  public boolean canAccessCompanyUsers(UUID companyId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    boolean isAppAdmin = authentication.getAuthorities().stream().
        anyMatch(a-> a.getAuthority().equals("ROLE_APPLICATION_ADMIN"));
    if(isAppAdmin) {
      return true;
    }

    boolean isCompanyUserManager = authentication.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals("ROLE_COMPANY_USER_MANAGER"));
    if(isCompanyUserManager) {
      if(authentication.getPrincipal() instanceof Jwt) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String tokenCompanyId = jwt.getClaimAsString("companyId");
        return tokenCompanyId != null && tokenCompanyId.equals(companyId.toString());
      }
    }

    return false;
  }

}
