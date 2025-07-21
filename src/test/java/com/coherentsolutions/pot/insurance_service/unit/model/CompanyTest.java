package com.coherentsolutions.pot.insurance_service.unit.model;

import com.coherentsolutions.pot.insurance_service.model.Address;
import com.coherentsolutions.pot.insurance_service.model.Company;
import com.coherentsolutions.pot.insurance_service.model.Phone;
import com.coherentsolutions.pot.insurance_service.enums.CompanyStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Company Entity Tests")
class CompanyTest {

    private Company company;
    private Address address;
    private Phone phone;

    @BeforeEach
    void setUp() {
        company = new Company();
        company.setId(UUID.randomUUID());
        company.setName("Test Company");
        company.setCountryCode("USA");
        company.setEmail("test@company.com");
        company.setWebsite("https://testcompany.com");
        company.setStatus(CompanyStatus.ACTIVE);
        company.setCreatedAt(Instant.now());
        company.setUpdatedAt(Instant.now());

        address = new Address();
        address.setCountry("USA");
        address.setCity("New York");
        address.setStreet("123 Main St");
        address.setBuilding("Building A");
        address.setRoom("Room 101");

        phone = new Phone();
        phone.setCode("+1");
        phone.setNumber("555-1234");

        company.setAddressData(List.of(address));
        company.setPhoneData(List.of(phone));
    }

    @Test
    @DisplayName("Should create company with all required fields")
    void shouldCreateCompanyWithAllRequiredFields() {
        // Then
        assertNotNull(company);
        assertNotNull(company.getId());
        assertEquals("Test Company", company.getName());
        assertEquals("USA", company.getCountryCode());
        assertEquals("test@company.com", company.getEmail());
        assertEquals("https://testcompany.com", company.getWebsite());
        assertEquals(CompanyStatus.ACTIVE, company.getStatus());
        assertEquals(1, company.getAddressData().size());
        assertEquals(1, company.getPhoneData().size());
    }

    @Test
    @DisplayName("Should update company fields")
    void shouldUpdateCompanyFields() {
        // Given
        String newName = "Updated Company";
        String newCountryCode = "CAN";
        String newEmail = "updated@company.com";
        String newWebsite = "https://updatedcompany.com";

        // When
        company.setName(newName);
        company.setCountryCode(newCountryCode);
        company.setEmail(newEmail);
        company.setWebsite(newWebsite);

        // Then
        assertEquals(newName, company.getName());
        assertEquals(newCountryCode, company.getCountryCode());
        assertEquals(newEmail, company.getEmail());
        assertEquals(newWebsite, company.getWebsite());
    }

    @Test
    @DisplayName("Should handle optional fields as null")
    void shouldHandleOptionalFieldsAsNull() {
        // Given
        Company minimalCompany = new Company();
        minimalCompany.setName("Minimal Company");
        minimalCompany.setCountryCode("USA");

        // When & Then
        assertNull(minimalCompany.getEmail());
        assertNull(minimalCompany.getWebsite());
        assertNull(minimalCompany.getAddressData());
        assertNull(minimalCompany.getPhoneData());
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
        company.setAddressData(List.of(address, address2));
        company.setPhoneData(List.of(phone, phone2));

        // Then
        assertEquals(2, company.getAddressData().size());
        assertEquals(2, company.getPhoneData().size());
        assertEquals("New York", company.getAddressData().get(0).getCity());
        assertEquals("Toronto", company.getAddressData().get(1).getCity());
        assertEquals("555-1234", company.getPhoneData().get(0).getNumber());
        assertEquals("555-5678", company.getPhoneData().get(1).getNumber());
    }

    @Test
    @DisplayName("Should handle different company statuses")
    void shouldHandleDifferentCompanyStatuses() {
        // When
        company.setStatus(CompanyStatus.ACTIVE);
        assertEquals(CompanyStatus.ACTIVE, company.getStatus());

        company.setStatus(CompanyStatus.DEACTIVATED);
        assertEquals(CompanyStatus.DEACTIVATED, company.getStatus());
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
        company.setCreatedBy(createdBy);
        company.setUpdatedBy(updatedBy);
        company.setCreatedAt(createdAt);
        company.setUpdatedAt(updatedAt);

        // Then
        assertEquals(createdBy, company.getCreatedBy());
        assertEquals(updatedBy, company.getUpdatedBy());
        assertEquals(createdAt, company.getCreatedAt());
        assertEquals(updatedAt, company.getUpdatedAt());
    }
} 