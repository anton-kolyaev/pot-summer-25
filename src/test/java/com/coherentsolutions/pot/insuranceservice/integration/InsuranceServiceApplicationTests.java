package com.coherentsolutions.pot.insuranceservice.integration;

import com.coherentsolutions.pot.insuranceservice.integration.containers.PostgresTestContainer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;


@SpringBootTest
@TestPropertySource(properties = "spring.main.allow-bean-definition-overriding=true")
@Import(TestSecurityConfig.class)
class InsuranceServiceApplicationTests extends PostgresTestContainer {

  @Test
  void contextLoads() {
  }

}
