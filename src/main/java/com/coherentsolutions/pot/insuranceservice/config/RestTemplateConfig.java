package com.coherentsolutions.pot.insuranceservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration for RestTemplate beans.
 *
 * <p>This configuration provides RestTemplate beans that can be used
 * throughout the application for HTTP operations.
 */
@Configuration
public class RestTemplateConfig {

  /**
   * Creates a RestTemplate bean for HTTP operations.
   *
   * @return a configured RestTemplate instance
   */
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
