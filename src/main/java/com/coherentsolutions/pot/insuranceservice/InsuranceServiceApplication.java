package com.coherentsolutions.pot.insuranceservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Main entry point for the Insurance Service Spring Boot application.
 */
@SpringBootApplication
@EnableJpaRepositories
@EnableJpaAuditing
public class InsuranceServiceApplication {

  /**
   * Starts the Spring Boot application.
   */
  public static void main(String[] args) {
    SpringApplication.run(InsuranceServiceApplication.class, args);
  }

}
