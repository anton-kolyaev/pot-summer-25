package com.coherentsolutions.pot.insuranceservice.integration.containers;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Abstract base class for integration tests using a PostgreSQL container. Utilizes Testcontainers
 * to run PostgreSQL in a Docker environment.
 */

public abstract class PostgresTestContainer {

  private static final String IMAGE_VERSION = "postgres:16.9";

  private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(IMAGE_VERSION);

  static {
    POSTGRES.start();
  }

  public static PostgreSQLContainer<?> getInstance() {
    return POSTGRES;
  }

  @DynamicPropertySource
  static void overrideProps(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRES::getUsername);
    registry.add("spring.datasource.password", POSTGRES::getPassword);
  }
}
