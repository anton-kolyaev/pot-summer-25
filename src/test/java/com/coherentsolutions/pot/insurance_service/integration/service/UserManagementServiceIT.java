package com.coherentsolutions.pot.insurance_service.integration.service;

import com.coherentsolutions.pot.insurance_service.dto.user.UserDto;
import com.coherentsolutions.pot.insurance_service.dto.user.UserFilter;
import com.coherentsolutions.pot.insurance_service.integration.IntegrationTestConfiguration;
import com.coherentsolutions.pot.insurance_service.integration.containers.PostgresTestContainer;
import com.coherentsolutions.pot.insurance_service.model.Company;
import com.coherentsolutions.pot.insurance_service.repository.CompanyRepository;
import com.coherentsolutions.pot.insurance_service.service.UserManagementService;

import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;

@Import(IntegrationTestConfiguration.class)
@SpringBootTest
@TestPropertySource(properties = "spring.main.allow-bean-definition-overriding=true")
class UserManagementServiceIT extends PostgresTestContainer {

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(userManagementService);
    }

      @Test
    void shouldCreateUserAndRetrieveWithFilters() {
        Company testCompany = new Company();
        testCompany.setName("Test Company");
        testCompany.setCountryCode("US"); 
        
        Company savedCompany = companyRepository.save(testCompany);

        UserDto newUser = UserDto.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@example.com")
                .username("jane.doe")
                .ssn("123-45-6789")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .companyId(savedCompany.getId())
                .build();

        UserDto createdUser = userManagementService.createUser(newUser);
        Assertions.assertNotNull(createdUser.getId());
        Assertions.assertEquals("Jane", createdUser.getFirstName());

        UserFilter filter = new UserFilter();
        Page<UserDto> usersPage = userManagementService.getUsersWithFilters(filter, PageRequest.of(0, 10));

        Assertions.assertFalse(usersPage.isEmpty());
        Assertions.assertTrue(usersPage.stream().anyMatch(u -> "jane.doe".equals(u.getUsername())));
    }
}
