package integration.com.coherentsolutions.pot.insurance_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.coherentsolutions.pot.insurance_service.InsuranceServiceApplication;

import integration.com.coherentsolutions.pot.insurance_service.containers.PostgresTestContainer;

@SpringBootTest(classes = InsuranceServiceApplication.class)
@TestPropertySource(properties = "spring.main.allow-bean-definition-overriding=true")
class InsuranceServiceApplicationIT extends PostgresTestContainer {

	@Test
	void contextLoads() {
	}

}
