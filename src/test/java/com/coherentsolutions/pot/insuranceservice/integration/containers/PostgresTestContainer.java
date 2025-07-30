package com.coherentsolutions.pot.insuranceservice.integration.containers;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Abstract base class for integration tests using a PostgreSQL container. Utilizes Testcontainers
 * to run PostgreSQL in a Docker environment.
 */

@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class PostgresTestContainer {

  private static final PostgreSQLContainer<?> POSTGRES;

  static {
    POSTGRES = new PostgreSQLContainer<>("postgres:16.9")
        .withDatabaseName("test-db")
        .withUsername("test")
        .withPassword("test");
    POSTGRES.start();
  }

  @DynamicPropertySource
  static void overrideProps(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRES::getUsername);
    registry.add("spring.datasource.password", POSTGRES::getPassword);
  }
}
