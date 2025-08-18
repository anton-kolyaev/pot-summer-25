package com.coherentsolutions.pot.insuranceservice.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

public class TestSecurityUtils {

  public static RequestPostProcessor adminUser() {
    return user("123e4567-e89b-12d3-a456-426614174000")
        .authorities(new SimpleGrantedAuthority("ROLE_APPLICATION_ADMIN"));
  }
}