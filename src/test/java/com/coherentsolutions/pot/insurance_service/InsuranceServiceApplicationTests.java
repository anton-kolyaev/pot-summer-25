package com.coherentsolutions.pot.insurance_service;

import com.coherentsolutions.pot.insurance_service.containers.PostgresTestContainer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "spring.main.allow-bean-definition-overriding=true")
class InsuranceServiceApplicationTests extends PostgresTestContainer {

	@Test
	void contextLoads() {
	}

}
