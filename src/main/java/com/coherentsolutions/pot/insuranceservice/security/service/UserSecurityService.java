package com.coherentsolutions.pot.insuranceservice.security.service;

import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class UserSecurityService {

  public boolean canAccessUserResource(UUID userId, String requiredRole) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      return false;
    }

    if (isAppAdmin(authentication)) {
      return true;
    }

    boolean isUsersResource = checkIfUsersResource(authentication, userId);


    boolean hasRequiredRole = authentication.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals(requiredRole));

    return hasRequiredRole && isUsersResource;
  }


  private boolean isAppAdmin(Authentication authentication) {
    return authentication.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals("ROLE_APPLICATION_ADMIN"));
  }

  private boolean checkIfUsersResource(Authentication authentication, UUID userId) {
    if (authentication.getPrincipal() instanceof Jwt jwt) {
      String tokenUserId = jwt.getClaimAsString("user_uuid");
      return userId != null && userId.toString().equals(tokenUserId);
    }
    return false;
  }

}
