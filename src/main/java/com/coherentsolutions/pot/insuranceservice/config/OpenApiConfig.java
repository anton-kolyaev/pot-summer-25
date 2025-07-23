package com.coherentsolutions.pot.insuranceservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for OpenAPI documentation in the Insurance Service application.
 */
@Configuration
public class OpenApiConfig {

  /**
   * Defines the OpenAPI specification with custom title, version, and description for Swagger UI
   * documentation.
   */
  @Bean
  public OpenAPI customOpenApi() {
    return new OpenAPI()
        .info(new Info()
            .title("Insurance Service API")
            .version("1.0")
            .description("API documentation for Insurance Service project"));
  }
}
