package com.coherentsolutions.pot.insuranceservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

@Configuration
public class JwtAuthConverterConfig {

  private static final String ROLES_CLAIM = "role";
  private static final String PRINCIPAL_CLAIM = "user_uuid";

  @Bean
  public JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtGrantedAuthoritiesConverter rolesConverter = new JwtGrantedAuthoritiesConverter();
    rolesConverter.setAuthoritiesClaimName(ROLES_CLAIM);
    rolesConverter.setAuthorityPrefix("ROLE_");
    JwtAuthenticationConverter authConverter = new JwtAuthenticationConverter();
    authConverter.setJwtGrantedAuthoritiesConverter(rolesConverter);
    authConverter.setPrincipalClaimName(PRINCIPAL_CLAIM);
    return authConverter;
  }
}
