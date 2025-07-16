package com.coherentsolutions.pot.insurance_service.mapper;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.coherentsolutions.pot.insurance_service.dto.user.UserDto;
import com.coherentsolutions.pot.insurance_service.enums.UserFunction;
import com.coherentsolutions.pot.insurance_service.model.Company;
import com.coherentsolutions.pot.insurance_service.model.User;
import com.coherentsolutions.pot.insurance_service.model.UserFunctionAssignment;

class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void mapToFunctions_shouldMapAssignmentsToFunctions() {
        UserFunctionAssignment assignment = new UserFunctionAssignment();
        assignment.setFunction(UserFunction.COMPANY_MANAGER);

        Set<UserFunction> result = userMapper.mapToFunctions(Set.of(assignment));

        assertEquals(Set.of(UserFunction.COMPANY_MANAGER), result);
    }

    @Test
    void mapToAssignments_shouldMapFunctionsToAssignments() {
        Set<UserFunctionAssignment> result = userMapper.mapToAssignments(Set.of(UserFunction.CONSUMER));

        assertEquals(1, result.size());
        assertEquals(UserFunction.CONSUMER, result.iterator().next().getFunction());
    }

    @Test
    void shouldMapUserToUserDtoCorrectly() {

        UUID companyId = UUID.randomUUID();
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("jdoe");
        user.setEmail("jdoe@example.com");
        user.setDateOfBirth(LocalDate.of(1990, 1, 1));
        user.setSsn("123-45-6789");
        user.setCompany(new Company());
        user.getCompany().setId(companyId);

        UserDto dto = userMapper.toDto(user);

        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
        assertEquals("jdoe", dto.getUsername());
        assertEquals("jdoe@example.com", dto.getEmail());
        assertEquals(LocalDate.of(1990, 1, 1), dto.getDateOfBirth());
        assertEquals("123-45-6789", dto.getSsn());
        assertEquals(companyId, dto.getCompanyId());
    }

    @Test
    void shouldMapUserDtoToUserCorrectly() {
        
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

        
        User user = userMapper.toEntity(dto);

        
        assertEquals("Jane", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        assertEquals("jsmith", user.getUsername());
        assertEquals("jane@example.com", user.getEmail());
        assertEquals(LocalDate.of(1995, 5, 15), user.getDateOfBirth());
        assertEquals("987-65-4321", user.getSsn());
        assertNotNull(user.getCompany());
        assertEquals(companyId, user.getCompany().getId());
    }

}
