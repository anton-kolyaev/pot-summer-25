package com.coherentsolutions.pot.insuranceservice.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

@Configuration
public class JwtAuthConverterConfig {

  @Value("${AUTH0_AUDIENCE}")
  private String authAudience;

  private static final String PRINCIPAL_CLAIM = "user_uuid";

  @Bean
  public JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();

    jwtConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
      Collection<GrantedAuthority> authorities = new ArrayList<>();

      String systemRolesClaim = authAudience + "/roles";
      List<String> systemRoles = jwt.getClaim(systemRolesClaim);
      if (systemRoles != null) {
        systemRoles.forEach(role ->
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role))
        );
      }

      String functionalRolesClaim = authAudience + "/functions";
      List<String> functionalRoles = jwt.getClaim(functionalRolesClaim);
      if (functionalRoles != null) {
        functionalRoles.forEach(role ->
            authorities.add(new SimpleGrantedAuthority("ROLE_FUNC_" + role))
        );
      }

      return authorities;
    });

    jwtConverter.setPrincipalClaimName(PRINCIPAL_CLAIM);

    return jwtConverter;
  }
}

