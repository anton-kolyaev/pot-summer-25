package com.coherentsolutions.pot.insuranceservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
  
  @Bean
  public OpenAPI customOpenApi(@Value("${AUTH0_DOMAIN}") String domain) {
    SecurityScheme oauthScheme = new SecurityScheme()
            .type(SecurityScheme.Type.OAUTH2)
            .flows(new OAuthFlows().authorizationCode(
                    new OAuthFlow()
                            .authorizationUrl("https://" + domain + "/authorize")
                            .tokenUrl("https://" + domain + "/oauth/token")
                            .scopes(new Scopes()
                                    .addString("openid",  "OpenID"))
            ));
    return new OpenAPI()
            .components(new Components()
                    .addSecuritySchemes("oauth2", oauthScheme))
            .addSecurityItem(new SecurityRequirement()
                    .addList("oauth2"))
            .info(new Info()
                    .title("Insurance Service API")
                    .version("1.0")
                    .description("API documentation for Insurance Service project"));
  }
}
