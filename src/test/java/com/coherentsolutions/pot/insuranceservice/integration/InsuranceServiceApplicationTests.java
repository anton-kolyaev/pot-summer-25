package com.coherentsolutions.pot.insuranceservice.integration;

import com.coherentsolutions.pot.insuranceservice.integration.containers.PostgresTestContainer;
import com.coherentsolutions.pot.insuranceservice.service.Auth0InvitationService;
import com.coherentsolutions.pot.insuranceservice.service.Auth0UserMetadataService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;


@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.main.allow-bean-definition-overriding=true",
    "auth0.enabled=false"
})
@Import(TestSecurityConfig.class)
class InsuranceServiceApplicationTests extends PostgresTestContainer {

  @MockBean
  private Auth0InvitationService auth0InvitationService;

  @MockBean
  private Auth0UserMetadataService auth0UserMetadataService;

  @Test
  void contextLoads() {
  }

}
