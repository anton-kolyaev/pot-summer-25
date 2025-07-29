package com.coherentsolutions.pot.insuranceservice.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcBuilderCustomizer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class MockMvcTestConfig {
  @Bean
    public MockMvcBuilderCustomizer defaultAuthHeader() {
    return builder -> builder.defaultRequest(
        get("/").with(jwt().jwt(jwt -> jwt.claim("sub", "test-user"))));
  }
}
