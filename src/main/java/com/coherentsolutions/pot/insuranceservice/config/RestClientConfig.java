package com.coherentsolutions.pot.insuranceservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Configuration for RestClient beans.
 *
 * <p>This configuration provides RestClient beans that can be used
 * throughout the application for HTTP operations.
 */
@Configuration
public class RestClientConfig {

  /**
   * Creates a RestClient bean for HTTP operations.
   *
   * @return a configured RestClient instance
   */
  @Bean
  public RestClient restClient() {
    return RestClient.builder().build();
  }
}
