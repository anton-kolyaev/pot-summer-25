package integration.com.coherensolutions.pot.insurance_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import integration.com.coherensolutions.pot.insurance_service.containers.PostgresTestContainer;

@SpringBootTest
@TestPropertySource(properties = "spring.main.allow-bean-definition-overriding=true")
class InsuranceServiceApplicationIT extends PostgresTestContainer {

	@Test
	void contextLoads() {
	}

}
