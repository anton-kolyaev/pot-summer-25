package com.coherentsolutions.pot.insuranceservice.integration;

import com.coherentsolutions.pot.insuranceservice.integration.containers.PostgresTestContainer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;


@SpringBootTest
@TestPropertySource(properties = {
    "spring.main.allow-bean-definition-overriding=true",
    "spring.profiles.active=test"
})
class InsuranceServiceApplicationTests extends PostgresTestContainer {

  @Test
  void contextLoads() {
  }

}
