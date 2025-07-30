package com.coherentsolutions.pot.insuranceservice;

import com.coherentsolutions.pot.insuranceservice.config.Auth0Properties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main entry point for the Insurance Service Spring Boot application.
 */
@SpringBootApplication
@EnableJpaRepositories
@EnableJpaAuditing
@EnableConfigurationProperties(Auth0Properties.class)
@EnableScheduling
public class InsuranceServiceApplication {

  /**
   * Starts the Spring Boot application.
   */
  public static void main(String[] args) {
    SpringApplication.run(InsuranceServiceApplication.class, args);
  }

}
