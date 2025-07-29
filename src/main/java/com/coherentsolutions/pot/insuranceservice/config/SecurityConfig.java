package com.coherentsolutions.pot.insuranceservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    
  @Bean
    public SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      JwtAuthenticationConverter jwtAuthConverter
  ) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(sm ->
        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
        .requestMatchers(
        "/swagger-ui/**",
        "/v3/api-docs/**"
    ).permitAll()
        .anyRequest().authenticated())
        .oauth2ResourceServer(oauth2 -> oauth2
        .jwt(jwt -> jwt
        .jwtAuthenticationConverter(jwtAuthConverter)
    )
        );
    return http.build();
  }
}
