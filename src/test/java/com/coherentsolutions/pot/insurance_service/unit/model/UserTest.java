package com.coherentsolutions.pot.insurance_service.unit.model;

import com.coherentsolutions.pot.insurance_service.enums.UserStatus;
import com.coherentsolutions.pot.insurance_service.model.Address;
import com.coherentsolutions.pot.insurance_service.model.Company;
import com.coherentsolutions.pot.insurance_service.model.Phone;
import com.coherentsolutions.pot.insurance_service.model.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("User Entity Tests")
class UserTest {

    private User user;
    private Company company;
    private Address address;
    private Phone phone;

    @BeforeEach
    void setUp() {
        company = new Company();
        company.setId(UUID.randomUUID());
        company.setName("Test Company");
        company.setCountryCode("USA");

        user = new User();
        user.setId(UUID.randomUUID());
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("johndoe");
        user.setEmail("john.doe@example.com");
        user.setDateOfBirth(LocalDate.of(1990, 1, 1));
        user.setSsn("123-45-6789");
        user.setCompany(company);
        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        address = new Address();
        address.setCountry("USA");
        address.setCity("New York");
        address.setStreet("123 Main St");
        address.setBuilding("Building A");
        address.setRoom("101");

        phone = new Phone();
        phone.setCode("+1");
        phone.setNumber("555-1234");

        user.setAddressData(List.of(address));
        user.setPhoneData(List.of(phone));
    }

    @Test
    @DisplayName("Should create user with all required fields")
    void shouldCreateUserWithAllRequiredFields() {
        assertThat(user).isNotNull();
        assertThat(user.getId()).isNotNull();
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getUsername()).isEqualTo("johndoe");
        assertThat(user.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(user.getDateOfBirth()).isEqualTo(LocalDate.of(1990, 1, 1));
        assertThat(user.getSsn()).isEqualTo("123-45-6789");
        assertThat(user.getCompany()).isEqualTo(company);
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(user.getAddressData()).hasSize(1);
        assertThat(user.getPhoneData()).hasSize(1);
    }

    @Test
    @DisplayName("Should update user fields")
    void shouldUpdateUserFields() {
        // Given
        String newFirstName = "Jane";
        String newLastName = "Smith";
        String newUsername = "janesmith";
        String newEmail = "jane.smith@example.com";
        String newSsn = "987-65-4321";
        LocalDate newDob = LocalDate.of(1985, 5, 15);

        // When
        user.setFirstName(newFirstName);
        user.setLastName(newLastName);
        user.setUsername(newUsername);
        user.setEmail(newEmail);
        user.setSsn(newSsn);
        user.setDateOfBirth(newDob);

        // Then
        assertThat(user.getFirstName()).isEqualTo(newFirstName);
        assertThat(user.getLastName()).isEqualTo(newLastName);
        assertThat(user.getUsername()).isEqualTo(newUsername);
        assertThat(user.getEmail()).isEqualTo(newEmail);
        assertThat(user.getSsn()).isEqualTo(newSsn);
        assertThat(user.getDateOfBirth()).isEqualTo(newDob);
    }

    @Test
    @DisplayName("Should handle optional address and phone fields as null")
    void shouldHandleOptionalFieldsAsNull() {
        // Given
        User minimalUser = new User();
        minimalUser.setFirstName("Minimal");
        minimalUser.setLastName("User");
        minimalUser.setUsername("minimaluser");
        minimalUser.setEmail("minimal@example.com");
        minimalUser.setSsn("111-22-3333");
        minimalUser.setDateOfBirth(LocalDate.of(2000, 1, 1));
        minimalUser.setCompany(company);

        // When & Then
        assertThat(minimalUser.getAddressData()).isNull();
        assertThat(minimalUser.getPhoneData()).isNull();
        assertThat(minimalUser.getFunctions()).isNull();
    }

    @Test
    @DisplayName("Should handle multiple addresses and phones")
    void shouldHandleMultipleAddressesAndPhones() {
        // Given
        Address address2 = new Address();
        address2.setCountry("Canada");
        address2.setCity("Toronto");
        address2.setStreet("456 Oak Ave");

        Phone phone2 = new Phone();
        phone2.setCode("+1");
        phone2.setNumber("555-5678");

        // When
        user.setAddressData(List.of(address, address2));
        user.setPhoneData(List.of(phone, phone2));

        // Then
        assertThat(user.getAddressData()).hasSize(2);
        assertThat(user.getPhoneData()).hasSize(2);
        assertThat(user.getAddressData().get(0).getCity()).isEqualTo("New York");
        assertThat(user.getAddressData().get(1).getCity()).isEqualTo("Toronto");
        assertThat(user.getPhoneData().get(0).getNumber()).isEqualTo("555-1234");
        assertThat(user.getPhoneData().get(1).getNumber()).isEqualTo("555-5678");
    }

    @Test
    @DisplayName("Should handle different user statuses")
    void shouldHandleDifferentUserStatuses() {
        // When
        user.setStatus(UserStatus.ACTIVE);
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);

        user.setStatus(UserStatus.INACTIVE);
        assertThat(user.getStatus()).isEqualTo(UserStatus.INACTIVE);
    }

    @Test
    @DisplayName("Should handle audit fields")
    void shouldHandleAuditFields() {
        // Given
        UUID createdBy = UUID.randomUUID();
        UUID updatedBy = UUID.randomUUID();
        Instant createdAt = Instant.now();
        Instant updatedAt = Instant.now();

        // When
        user.setCreatedBy(createdBy);
        user.setUpdatedBy(updatedBy);
        user.setCreatedAt(createdAt);
        user.setUpdatedAt(updatedAt);

        // Then
        assertThat(user.getCreatedBy()).isEqualTo(createdBy);
        assertThat(user.getUpdatedBy()).isEqualTo(updatedBy);
        assertThat(user.getCreatedAt()).isEqualTo(createdAt);
        assertThat(user.getUpdatedAt()).isEqualTo(updatedAt);
    }
}
