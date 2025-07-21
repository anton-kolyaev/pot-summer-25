package com.coherentsolutions.pot.insurance_service.integration.service;

import com.coherentsolutions.pot.insurance_service.integration.containers.PostgresTestContainer;
import com.coherentsolutions.pot.insurance_service.service.UserManagementService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "spring.main.allow-bean-definition-overriding=true")
class UserManagementServiceIT extends PostgresTestContainer {

    @Autowired
    private UserManagementService userManagementService;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(userManagementService);
    }
}
