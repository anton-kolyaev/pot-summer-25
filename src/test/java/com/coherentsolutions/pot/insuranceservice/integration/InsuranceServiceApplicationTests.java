package com.coherentsolutions.pot.insuranceservice.integration;

import com.coherentsolutions.pot.insuranceservice.integration.containers.PostgresTestContainer;
import com.coherentsolutions.pot.insuranceservice.service.Auth0UserManagementService;
import com.coherentsolutions.pot.insuranceservice.service.UserInvitationService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;


@SpringBootTest
@TestPropertySource(properties = {
    "spring.main.allow-bean-definition-overriding=true",
    "auth0.enabled=false"
})
@Import(TestSecurityConfig.class)
class InsuranceServiceApplicationTests extends PostgresTestContainer {

  @MockBean
  private Auth0UserManagementService auth0UserManagementService;

  @MockBean
  private UserInvitationService userInvitationService;

  @Test
  void contextLoads() {
  }

}
