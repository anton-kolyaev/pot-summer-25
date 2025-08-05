package com.coherentsolutions.pot.insuranceservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

/**
 * Test configuration for integration tests. Provides custom ObjectMapper with JavaTimeModule
 * support and mocked ManagementAPI for Auth0 integration tests.
 */
@TestConfiguration
@Import({TestSecurityConfig.class, MockMvcTestConfig.class})
public class IntegrationTestConfiguration {

  @Bean
  @Primary
  public ObjectMapper objectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    return objectMapper;
  }
}
