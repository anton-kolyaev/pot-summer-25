package com.coherentsolutions.pot.insurance_service.unit.mapper;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.coherentsolutions.pot.insurance_service.dto.user.UserDto;
import com.coherentsolutions.pot.insurance_service.enums.UserFunction;
import com.coherentsolutions.pot.insurance_service.mapper.UserMapper;
import com.coherentsolutions.pot.insurance_service.model.Company;
import com.coherentsolutions.pot.insurance_service.model.User;
import com.coherentsolutions.pot.insurance_service.model.UserFunctionAssignment;

class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    @DisplayName("Should map UserFunctionAssignment set to UserFunction set")
    void mapToFunctions_shouldMapAssignmentsToFunctions() {
        // Given
        UserFunctionAssignment assignment = new UserFunctionAssignment();
        assignment.setFunction(UserFunction.COMPANY_MANAGER);

        // When
        Set<UserFunction> result = userMapper.mapToFunctions(Set.of(assignment));

        // Then
        assertEquals(Set.of(UserFunction.COMPANY_MANAGER), result);
    }

    @Test
    @DisplayName("Should map UserFunction set to UserFunctionAssignment set")
    void mapToAssignments_shouldMapFunctionsToAssignments() {
        // Given
        Set<UserFunction> functions = Set.of(UserFunction.CONSUMER);

        // When
        Set<UserFunctionAssignment> result = userMapper.mapToAssignments(functions);

        // Then
        assertEquals(1, result.size());
        assertEquals(UserFunction.CONSUMER, result.iterator().next().getFunction());
    }

    @Test
    @DisplayName("Should map User entity to UserDto correctly")
    void shouldMapUserToUserDtoCorrectly() {
        // Given
        UUID companyId = UUID.randomUUID();
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("jdoe");
        user.setEmail("jdoe@example.com");
        user.setDateOfBirth(LocalDate.of(1990, 1, 1));
        user.setSsn("123-45-6789");

        Company company = new Company();
        company.setId(companyId);
        user.setCompany(company);

        // When
        UserDto dto = userMapper.toDto(user);

        // Then
        assertEquals(user.getFirstName(), dto.getFirstName());
        assertEquals(user.getLastName(), dto.getLastName());
        assertEquals(user.getUsername(), dto.getUsername());
        assertEquals(user.getEmail(), dto.getEmail());
        assertEquals(user.getDateOfBirth(), dto.getDateOfBirth());
        assertEquals(user.getSsn(), dto.getSsn());
        assertEquals(user.getCompany().getId(), dto.getCompanyId());
    }

    @Test
    @DisplayName("Should map UserDto to User entity correctly")
    void shouldMapUserDtoToUserCorrectly() {
        // Given
        UUID companyId = UUID.randomUUID();
        UserDto dto = UserDto.builder()
                .firstName("Jane")
                .lastName("Smith")
                .username("jsmith")
                .email("jane@example.com")
                .dateOfBirth(LocalDate.of(1995, 5, 15))
                .ssn("987-65-4321")
                .companyId(companyId)
                .build();

        // When
        User user = userMapper.toEntity(dto);

        // Then
        assertEquals(dto.getFirstName(), user.getFirstName());
        assertEquals(dto.getLastName(), user.getLastName());
        assertEquals(dto.getUsername(), user.getUsername());
        assertEquals(dto.getEmail(), user.getEmail());
        assertEquals(dto.getDateOfBirth(), user.getDateOfBirth());
        assertEquals(dto.getSsn(), user.getSsn());
        assertNotNull(user.getCompany());
        assertEquals(dto.getCompanyId(), user.getCompany().getId());
    }

}
