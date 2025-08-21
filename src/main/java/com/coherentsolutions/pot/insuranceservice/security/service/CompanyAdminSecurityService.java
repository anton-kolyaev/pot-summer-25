package com.coherentsolutions.pot.insuranceservice.security.service;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class CompanyAdminSecurityService {

  public boolean canAccessCompanyResource(UUID companyId, String requiredRole) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      return false;
    }

    if (isAppAdmin(authentication)) {
      return true;
    }

    boolean belongsToCompany = checkIfBelongsToCompany(authentication, companyId);
    boolean hasRequiredRole = authentication.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals(requiredRole));
    System.out.println(requiredRole);

    authentication.getAuthorities().forEach(authority ->
        System.out.println(authority.getAuthority())
    );
    System.out.println(hasRequiredRole);
    System.out.println(belongsToCompany);
    return hasRequiredRole && belongsToCompany;
  }

  private boolean isAppAdmin(Authentication authentication) {
    return authentication.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals("ROLE_APPLICATION_ADMIN"));
  }

  @Value("${AUTH0_AUDIENCE:}")
  private String authAudience;

  private boolean checkIfBelongsToCompany(Authentication authentication, UUID companyId) {
    if (authentication.getPrincipal() instanceof Jwt jwt) {
      String tokenCompanyId = jwt.getClaimAsString(authAudience + "/company_id");
      return tokenCompanyId != null && tokenCompanyId.equals(companyId.toString());
    }
    return false;
  }

}

