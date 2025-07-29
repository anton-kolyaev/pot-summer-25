package com.coherentsolutions.pot.insuranceservice.integration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@TestConfiguration
public class TestSecurityConfig {
  @Bean
    public JwtDecoder jwtDecoder() {
    return tokenValue -> Jwt.withTokenValue(tokenValue)
    .header("alg", "none")
    .claim("sub", "test-user")
    .build();
  }
}
