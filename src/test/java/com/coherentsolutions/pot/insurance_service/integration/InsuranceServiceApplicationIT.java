package com.coherentsolutions.pot.insurance_service.integration;

import com.coherentsolutions.pot.insurance_service.integration.containers.PostgresTestContainer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.coherentsolutions.pot.insurance_service.InsuranceServiceApplication;

@SpringBootTest(classes = InsuranceServiceApplication.class)
@TestPropertySource(properties = "spring.main.allow-bean-definition-overriding=true")
class InsuranceServiceApplicationIT extends PostgresTestContainer {

	@Test
	void contextLoads() {
	}

}
