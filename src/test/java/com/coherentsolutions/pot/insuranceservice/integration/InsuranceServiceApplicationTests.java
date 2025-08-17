package com.coherentsolutions.pot.insuranceservice.integration;

import com.coherentsolutions.pot.insuranceservice.integration.containers.PostgresTestContainer;
import com.coherentsolutions.pot.insuranceservice.service.Auth0InvitationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;


@SpringBootTest
@TestPropertySource(properties = {
    "spring.main.allow-bean-definition-overriding=true",
    "auth0.enabled=false"
})
@Import(TestSecurityConfig.class)
@ExtendWith(MockitoExtension.class)
class InsuranceServiceApplicationTests extends PostgresTestContainer {

  @Mock
  private Auth0InvitationService auth0InvitationService;

  @Test
  void contextLoads() {
  }

}
