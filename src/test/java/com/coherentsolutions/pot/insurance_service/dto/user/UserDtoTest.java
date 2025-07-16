package com.coherentsolutions.pot.insurance_service.dto.user;

import com.coherentsolutions.pot.insurance_service.dto.AddressDto;
import com.coherentsolutions.pot.insurance_service.dto.PhoneDto;
import com.coherentsolutions.pot.insurance_service.enums.UserFunction;
import com.coherentsolutions.pot.insurance_service.enums.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("User DTO Tests")
class UserDtoTest {

    private UUID testUserId;
    private AddressDto testAddressDto;
    private PhoneDto testPhoneDto;
    private UUID testCompanyId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testCompanyId = UUID.randomUUID();

        testAddressDto = AddressDto.builder()
                .country("USA")
                .city("New York")
                .street("123 Main St")
                .building("Building A")
                .room("101")
                .build();

        testPhoneDto = PhoneDto.builder()
                .code("+1")
                .number("555-1234")
                .build();
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
                .addressData(List.of(testAddressDto))
                .phoneData(List.of(testPhoneDto))
                .functions(Set.of(UserFunction.COMPANY_CLAIM_MANAGER, UserFunction.CONSUMER))
                .status(UserStatus.ACTIVE)
                .companyId(testCompanyId)
                .build();

        // Then
        assertThat(userDto).isNotNull();
        assertThat(userDto.getId()).isEqualTo(testUserId);
        assertThat(userDto.getFirstName()).isEqualTo("John");
        assertThat(userDto.getLastName()).isEqualTo("Doe");
        assertThat(userDto.getUsername()).isEqualTo("jdoe");
        assertThat(userDto.getEmail()).isEqualTo("jdoe@example.com");
        assertThat(userDto.getDateOfBirth()).isEqualTo(dob);
        assertThat(userDto.getSsn()).isEqualTo("123-45-6789");
        assertThat(userDto.getAddressData()).hasSize(1);
        assertThat(userDto.getPhoneData()).hasSize(1);
        assertThat(userDto.getFunctions()).contains(UserFunction.COMPANY_CLAIM_MANAGER, UserFunction.CONSUMER);
        assertThat(userDto.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(userDto.getCompanyId()).isEqualTo(testCompanyId);
    }

    @Test
    @DisplayName("Should create UserDto with minimal fields")
    void shouldCreateUserDtoWithMinimalFields() {
        // When
        UserDto userDto = UserDto.builder()
                .username("jdoe")
                .build();

        // Then
        assertThat(userDto).isNotNull();
        assertThat(userDto.getUsername()).isEqualTo("jdoe");
        assertThat(userDto.getId()).isNull();
        assertThat(userDto.getFirstName()).isNull();
        assertThat(userDto.getLastName()).isNull();
        assertThat(userDto.getEmail()).isNull();
        assertThat(userDto.getDateOfBirth()).isNull();
        assertThat(userDto.getSsn()).isNull();
        assertThat(userDto.getAddressData()).isNull();
        assertThat(userDto.getPhoneData()).isNull();
        assertThat(userDto.getFunctions()).isNull();
        assertThat(userDto.getStatus()).isNull();
        assertThat(userDto.getCompanyId()).isNull();
    }

    @Test
    @DisplayName("Should handle multiple addresses and phones")
    void shouldHandleMultipleAddressesAndPhones() {
        // Given
        AddressDto address2 = AddressDto.builder()
                .country("Canada")
                .city("Toronto")
                .street("456 Maple St")
                .build();

        PhoneDto phone2 = PhoneDto.builder()
                .code("+1")
                .number("555-5678")
                .build();

        // When
        UserDto userDto = UserDto.builder()
                .username("jdoe")
                .addressData(List.of(testAddressDto, address2))
                .phoneData(List.of(testPhoneDto, phone2))
                .build();

        // Then
        assertThat(userDto.getAddressData()).hasSize(2);
        assertThat(userDto.getPhoneData()).hasSize(2);
        assertThat(userDto.getAddressData().get(0).getCity()).isEqualTo("New York");
        assertThat(userDto.getAddressData().get(1).getCity()).isEqualTo("Toronto");
        assertThat(userDto.getPhoneData().get(0).getNumber()).isEqualTo("555-1234");
        assertThat(userDto.getPhoneData().get(1).getNumber()).isEqualTo("555-5678");
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
        assertThat(userDto.getEmail()).isNull();
        assertThat(userDto.getSsn()).isNull();
        assertThat(userDto.getAddressData()).isNull();
        assertThat(userDto.getPhoneData()).isNull();
        assertThat(userDto.getFunctions()).isNull();
        assertThat(userDto.getStatus()).isNull();
        assertThat(userDto.getCompanyId()).isNull();
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
        assertThat(activeUser.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(activeUser.getFunctions()).contains(UserFunction.CONSUMER);

        assertThat(inactiveUser.getStatus()).isEqualTo(UserStatus.INACTIVE);
        assertThat(inactiveUser.getFunctions()).contains(UserFunction.COMPANY_CLAIM_MANAGER);
    }
}
