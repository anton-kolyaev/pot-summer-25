package com.coherentsolutions.pot.insuranceservice.security.service;

import com.coherentsolutions.pot.insuranceservice.dto.claim.ClaimDto;
import com.coherentsolutions.pot.insuranceservice.dto.claim.ClaimFilter;
import com.coherentsolutions.pot.insuranceservice.service.ClaimManagementService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service("claimSecurity")
@RequiredArgsConstructor
public class ClaimSecurityService {

  private static final String ROLE_APP_ADMIN = "ROLE_APPLICATION_ADMIN";
  private static final String ROLE_CLAIM_MANAGER = "ROLE_FUNC_COMPANY_CLAIM_MANAGER";
  private static final String ROLE_CONSUMER = "ROLE_CONSUMER";

  private final ClaimManagementService claimManagementService;

  public boolean canList(ClaimFilter filter) {
    Authentication auth = currentAuth();
    if (auth == null) {
      return false;
    }

    if (has(auth, ROLE_APP_ADMIN)) {
      return true;
    }

    if (has(auth, ROLE_CLAIM_MANAGER)) {
      UUID companyId = companyIdFrom(auth);
      if (companyId == null) {
        return false;
      }
      if (filter.getCompanyId() == null) {
        filter.setCompanyId(companyId);
      }
      return true;
    }

    if (has(auth, ROLE_CONSUMER)) {
      UUID me = userIdFrom(auth);
      if (me == null) {
        return false;
      }
      if (filter.getUserId() != null && !me.equals(filter.getUserId())) {
        return false;
      }
      filter.setUserId(me);
      return true;
    }

    return false;
  }

  public boolean canCreate(ClaimDto request) {
    Authentication auth = currentAuth();
    if (auth == null) {
      return false;
    }

    if (has(auth, ROLE_APP_ADMIN)) {
      return true;
    }

    if (has(auth, ROLE_CONSUMER)) {
      UUID me = userIdFrom(auth);
      UUID target = request != null && request.getConsumer() != null
          ? request.getConsumer().getUserId() : null;
      return me != null && me.equals(target);
    }
    return false;
  }

  public boolean canProcess(UUID claimId) {
    Authentication auth = currentAuth();
    if (auth == null) {
      return false;
    }

    if (has(auth, ROLE_APP_ADMIN)) {
      return true;
    }

    if (has(auth, ROLE_CLAIM_MANAGER)) {
      var own = claimManagementService.getClaimOwnership(claimId);
      if (own == null) {
        return true;
      }
      UUID myCompany = companyIdFrom(auth);
      return myCompany != null && myCompany.equals(own.companyId());
    }
    return false;
  }

  public boolean canRead(UUID claimId) {
    Authentication auth = currentAuth();
    if (auth == null) {
      return false;
    }

    if (has(auth, ROLE_APP_ADMIN)) {
      return true;
    }

    var own = claimManagementService.getClaimOwnership(claimId);
    if (own == null) {
      return true;
    }

    if (has(auth, ROLE_CLAIM_MANAGER)) {
      UUID myCompany = companyIdFrom(auth);
      if (myCompany != null && myCompany.equals(own.companyId())) {
        return true;
      }
    }

    if (has(auth, ROLE_CONSUMER)) {
      UUID me = userIdFrom(auth);
      return me != null && me.equals(own.userId());
    }
    return false;
  }

  public boolean canListUserClaims(UUID userId) {
    Authentication auth = currentAuth();
    if (auth == null) {
      return false;
    }

    if (has(auth, ROLE_APP_ADMIN)) {
      return true;
    }

    if (has(auth, ROLE_CONSUMER)) {
      UUID me = userIdFrom(auth);
      return me != null && me.equals(userId);
    }
    return false;
  }

  public boolean canCreateForUser(UUID userId, ClaimDto request) {
    Authentication auth = currentAuth();
    if (auth == null) {
      return false;
    }

    if (has(auth, ROLE_APP_ADMIN)) {
      return true;
    }

    if (has(auth, ROLE_CONSUMER)) {
      UUID me = userIdFrom(auth);
      UUID bodyUser = (request != null && request.getConsumer() != null)
          ? request.getConsumer().getUserId() : null;
      return me != null && me.equals(userId) && userId.equals(bodyUser);
    }
    return false;
  }

  private Authentication currentAuth() {
    return SecurityContextHolder.getContext().getAuthentication();
  }

  private boolean has(Authentication auth, String role) {
    return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(role));
  }

  private UUID companyIdFrom(Authentication auth) {
    if (auth.getPrincipal() instanceof Jwt jwt) {
      String v = jwt.getClaimAsString("company_id");
      return v != null ? UUID.fromString(v) : null;
    }
    return null;
  }

  private UUID userIdFrom(Authentication auth) {
    if (auth.getPrincipal() instanceof Jwt jwt) {
      String v = jwt.getClaimAsString("user_uuid");
      if (v == null) {
        v = jwt.getClaimAsString("user_id");
      }
      return v != null ? UUID.fromString(v) : null;
    }
    return null;
  }
}

