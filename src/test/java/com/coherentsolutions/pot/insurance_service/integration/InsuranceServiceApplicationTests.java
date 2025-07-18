package com.coherentsolutions.pot.insurance_service.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.coherentsolutions.pot.insurance_service.integration.containers.PostgresTestContainer;

@SpringBootTest
@TestPropertySource(properties = "spring.main.allow-bean-definition-overriding=true")
class InsuranceServiceApplicationTests extends PostgresTestContainer {

	@Test
	void contextLoads() {
	}

}
