package com.coherentsolutions.pot.insuranceservice.integration;

import com.coherentsolutions.pot.insuranceservice.InsuranceServiceApplication;
import com.coherentsolutions.pot.insuranceservice.integration.containers.PostgresTestContainer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = InsuranceServiceApplication.class)
@TestPropertySource(properties = "spring.main.allow-bean-definition-overriding=true")
class InsuranceServiceApplicationIT extends PostgresTestContainer {

  @Test
  void contextLoads() {}
}
