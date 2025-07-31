package com.coherentsolutions.pot.insuranceservice.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcBuilderCustomizer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class MockMvcTestConfig {

  private static final String TEST_UUID = "11111111-2222-3333-4444-555555555555";

  @Bean
  public MockMvcBuilderCustomizer defaultAuthHeader() {
    return builder -> builder.defaultRequest(
        get("/").with(
            jwt().jwt(jwt -> jwt
                .subject(TEST_UUID)
            )
        )
    );
  }
}
