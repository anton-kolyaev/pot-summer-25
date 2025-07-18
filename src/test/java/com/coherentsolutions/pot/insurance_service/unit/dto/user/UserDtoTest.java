package com.coherentsolutions.pot.insurance_service.unit.dto.user;

import com.coherentsolutions.pot.insurance_service.dto.user.UserDto;
import com.coherentsolutions.pot.insurance_service.enums.UserFunction;
import com.coherentsolutions.pot.insurance_service.enums.UserStatus;
import com.coherentsolutions.pot.insurance_service.model.Address;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User DTO Tests")
class UserDtoTest {

    private UUID testUserId;
    private UUID testCompanyId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testCompanyId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Should create UserDto with all fields using builder")
    void shouldCreateUserDtoWithAllFields() {
        // Given
        LocalDate dob = LocalDate.of(1990, 1, 1);

        // When
        UserDto userDto = UserDto.builder()
                .id(testUserId)
                .firstName("John")
                .lastName("Doe")
                .username("jdoe")
                .email("jdoe@example.com")
                .dateOfBirth(dob)
                .ssn("123-45-6789")
                .addressData(List.of(new Address())) // Assuming empty list for simplicity
                .phoneData(List.of())
                .functions(Set.of(UserFunction.COMPANY_CLAIM_MANAGER, UserFunction.CONSUMER))
                .status(UserStatus.ACTIVE)
                .companyId(testCompanyId)
                .build();

        // Then
        assertNotNull(userDto);
        assertEquals(testUserId, userDto.getId());
        assertEquals("John", userDto.getFirstName());
        assertEquals("Doe", userDto.getLastName());
        assertEquals("jdoe", userDto.getUsername());
        assertEquals("jdoe@example.com", userDto.getEmail());
        assertEquals(dob, userDto.getDateOfBirth());
        assertEquals("123-45-6789", userDto.getSsn());
        assertEquals(1, userDto.getAddressData().size());
        assertEquals(0, userDto.getPhoneData().size());
        assertTrue(userDto.getFunctions().contains(UserFunction.COMPANY_CLAIM_MANAGER));
        assertTrue(userDto.getFunctions().contains(UserFunction.CONSUMER));
        assertEquals(UserStatus.ACTIVE, userDto.getStatus());
        assertEquals(testCompanyId, userDto.getCompanyId());
    }

    @Test
    @DisplayName("Should create UserDto with minimal fields")
    void shouldCreateUserDtoWithMinimalFields() {
        // When
        UserDto userDto = UserDto.builder()
                .username("jdoe")
                .build();

        // Then
        assertNotNull(userDto);
        assertEquals("jdoe", userDto.getUsername());
        assertNull(userDto.getId());
        assertNull(userDto.getFirstName());
        assertNull(userDto.getLastName());
        assertNull(userDto.getEmail());
        assertNull(userDto.getDateOfBirth());
        assertNull(userDto.getSsn());
        assertNull(userDto.getAddressData());
        assertNull(userDto.getPhoneData());
        assertNull(userDto.getFunctions());
        assertNull(userDto.getStatus());
        assertNull(userDto.getCompanyId());
    }

    @Test
    @DisplayName("Should handle optional fields as null")
    void shouldHandleOptionalFieldsAsNull() {
        // When
        UserDto userDto = UserDto.builder()
                .username("jdoe")
                .email(null)
                .ssn(null)
                .addressData(null)
                .phoneData(null)
                .functions(null)
                .status(null)
                .companyId(null)
                .build();

        // Then
        assertNull(userDto.getEmail());
        assertNull(userDto.getSsn());
        assertNull(userDto.getAddressData());
        assertNull(userDto.getPhoneData());
        assertNull(userDto.getFunctions());
        assertNull(userDto.getStatus());
        assertNull(userDto.getCompanyId());
    }

    @Test
    @DisplayName("Should handle different user statuses and functions")
    void shouldHandleDifferentUserStatusesAndFunctions() {
        // When
        UserDto activeUser = UserDto.builder()
                .username("activeUser")
                .status(UserStatus.ACTIVE)
                .functions(Set.of(UserFunction.CONSUMER))
                .build();

        UserDto inactiveUser = UserDto.builder()
                .username("inactiveUser")
                .status(UserStatus.INACTIVE)
                .functions(Set.of(UserFunction.COMPANY_CLAIM_MANAGER))
                .build();

        // Then
        assertEquals(UserStatus.ACTIVE, activeUser.getStatus());
        assertTrue(activeUser.getFunctions().contains(UserFunction.CONSUMER));

        assertEquals(UserStatus.INACTIVE, inactiveUser.getStatus());
        assertTrue(inactiveUser.getFunctions().contains(UserFunction.COMPANY_CLAIM_MANAGER));
    }
}
