package com.coherentsolutions.pot.insurance_service.unit.dto;

import com.coherentsolutions.pot.insurance_service.dto.CompanyDto;
import com.coherentsolutions.pot.insurance_service.model.Address;
import com.coherentsolutions.pot.insurance_service.model.Phone;
import com.coherentsolutions.pot.insurance_service.enums.CompanyStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Company DTO Tests")
class CompanyDtoTest {

    private UUID testCompanyId;
    private Address testAddress;
    private Phone testPhone;

    @BeforeEach
    void setUp() {
        testCompanyId = UUID.randomUUID();
        testAddress = new Address();
        testAddress.setCountry("USA");
        testAddress.setCity("New York");
        testAddress.setStreet("123 Main St");
        testAddress.setBuilding("Building A");
        testAddress.setRoom("Room 101");

        testPhone = new Phone();
        testPhone.setCode("+1");
        testPhone.setNumber("555-1234");
    }

    @Test
    @DisplayName("Should create company DTO with builder pattern")
    void shouldCreateCompanyDtoWithBuilderPattern() {
        // Given
        UUID createdBy = UUID.randomUUID();
        UUID updatedBy = UUID.randomUUID();
        Instant createdAt = Instant.now();
        Instant updatedAt = Instant.now();

        // When
        CompanyDto companyDto = CompanyDto.builder()
                .id(testCompanyId)
                .name("Test Company")
                .countryCode("USA")
                .email("test@company.com")
                .website("https://testcompany.com")
                .status(CompanyStatus.ACTIVE)
                .addressData(List.of(testAddress))
                .phoneData(List.of(testPhone))
                .createdBy(createdBy)
                .createdAt(createdAt)
                .updatedBy(updatedBy)
                .updatedAt(updatedAt)
                .build();

        // Then
        assertNotNull(companyDto);
        assertEquals(testCompanyId, companyDto.getId());
        assertEquals("Test Company", companyDto.getName());
        assertEquals("USA", companyDto.getCountryCode());
        assertEquals("test@company.com", companyDto.getEmail());
        assertEquals("https://testcompany.com", companyDto.getWebsite());
        assertEquals(CompanyStatus.ACTIVE, companyDto.getStatus());
        assertEquals(1, companyDto.getAddressData().size());
        assertEquals(1, companyDto.getPhoneData().size());
        assertEquals(createdBy, companyDto.getCreatedBy());
        assertEquals(createdAt, companyDto.getCreatedAt());
        assertEquals(updatedBy, companyDto.getUpdatedBy());
        assertEquals(updatedAt, companyDto.getUpdatedAt());
    }

    @Test
    @DisplayName("Should create company DTO with minimal fields")
    void shouldCreateCompanyDtoWithMinimalFields() {
        // When
        CompanyDto companyDto = CompanyDto.builder()
                .name("Test Company")
                .countryCode("USA")
                .build();

        // Then
        assertNotNull(companyDto);
        assertEquals("Test Company", companyDto.getName());
        assertEquals("USA", companyDto.getCountryCode());
        assertNull(companyDto.getId());
        assertNull(companyDto.getEmail());
        assertNull(companyDto.getWebsite());
        assertNull(companyDto.getStatus());
        assertNull(companyDto.getAddressData());
        assertNull(companyDto.getPhoneData());
        assertNull(companyDto.getCreatedBy());
        assertNull(companyDto.getCreatedAt());
        assertNull(companyDto.getUpdatedBy());
        assertNull(companyDto.getUpdatedAt());
    }

    @Test
    @DisplayName("Should handle different company statuses")
    void shouldHandleDifferentCompanyStatuses() {
        // When
        CompanyDto activeCompany = CompanyDto.builder()
                .name("Active Company")
                .status(CompanyStatus.ACTIVE)
                .build();

        CompanyDto deactivatedCompany = CompanyDto.builder()
                .name("Deactivated Company")
                .status(CompanyStatus.DEACTIVATED)
                .build();

        // Then
        assertEquals(CompanyStatus.ACTIVE, activeCompany.getStatus());
        assertEquals(CompanyStatus.DEACTIVATED, deactivatedCompany.getStatus());
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
        CompanyDto companyDto = CompanyDto.builder()
                .name("Test Company")
                .addressData(List.of(testAddress, address2))
                .phoneData(List.of(testPhone, phone2))
                .build();

        // Then
        assertEquals(2, companyDto.getAddressData().size());
        assertEquals(2, companyDto.getPhoneData().size());
        assertEquals("New York", companyDto.getAddressData().get(0).getCity());
        assertEquals("Toronto", companyDto.getAddressData().get(1).getCity());
        assertEquals("555-1234", companyDto.getPhoneData().get(0).getNumber());
        assertEquals("555-5678", companyDto.getPhoneData().get(1).getNumber());
    }

    @Test
    @DisplayName("Should handle optional fields as null")
    void shouldHandleOptionalFieldsAsNull() {
        // When
        CompanyDto companyDto = CompanyDto.builder()
                .name("Test Company")
                .countryCode("USA")
                .email(null)
                .website(null)
                .addressData(null)
                .phoneData(null)
                .build();

        // Then
        assertNull(companyDto.getEmail());
        assertNull(companyDto.getWebsite());
        assertNull(companyDto.getAddressData());
        assertNull(companyDto.getPhoneData());
    }

    @Test
    @DisplayName("Should create DTO with null name - no validation at DTO level")
    void shouldCreateDtoWithNullName() {
        // When
        CompanyDto companyDto = CompanyDto.builder()
                .name(null)
                .countryCode("USA")
                .build();

        // Then
        assertNotNull(companyDto);
        assertNull(companyDto.getName());
        assertEquals("USA", companyDto.getCountryCode());
    }

    @Test
    @DisplayName("Should create DTO with null countryCode - no validation at DTO level")
    void shouldCreateDtoWithNullCountryCode() {
        // When
        CompanyDto companyDto = CompanyDto.builder()
                .name("Test Company")
                .countryCode(null)
                .build();

        // Then
        assertNotNull(companyDto);
        assertEquals("Test Company", companyDto.getName());
        assertNull(companyDto.getCountryCode());
    }

    @Test
    @DisplayName("Should create DTO with empty name - no validation at DTO level")
    void shouldCreateDtoWithEmptyName() {
        // When
        CompanyDto companyDto = CompanyDto.builder()
                .name("")
                .countryCode("USA")
                .build();

        // Then
        assertNotNull(companyDto);
        assertEquals("", companyDto.getName());
        assertEquals("USA", companyDto.getCountryCode());
    }

    @Test
    @DisplayName("Should create DTO with empty countryCode - no validation at DTO level")
    void shouldCreateDtoWithEmptyCountryCode() {
        // When
        CompanyDto companyDto = CompanyDto.builder()
                .name("Test Company")
                .countryCode("")
                .build();

        // Then
        assertNotNull(companyDto);
        assertEquals("Test Company", companyDto.getName());
        assertEquals("", companyDto.getCountryCode());
    }

    @Test
    @DisplayName("Should create DTO with invalid email format - no validation at DTO level")
    void shouldCreateDtoWithInvalidEmailFormat() {
        // When
        CompanyDto companyDto = CompanyDto.builder()
                .name("Test Company")
                .countryCode("USA")
                .email("invalid-email-format")
                .build();

        // Then
        assertNotNull(companyDto);
        assertEquals("Test Company", companyDto.getName());
        assertEquals("USA", companyDto.getCountryCode());
        assertEquals("invalid-email-format", companyDto.getEmail());
    }

    @Test
    @DisplayName("Should create DTO with all null required fields - no validation at DTO level")
    void shouldCreateDtoWithAllNullRequiredFields() {
        // When
        CompanyDto companyDto = CompanyDto.builder()
                .name(null)
                .countryCode(null)
                .build();

        // Then
        assertNotNull(companyDto);
        assertNull(companyDto.getName());
        assertNull(companyDto.getCountryCode());
    }

    @Test
    @DisplayName("Should demonstrate validation strategy - DTO has no validation, Entity has @NotBlank")
    void shouldDemonstrateValidationStrategy() {
        // Given: CompanyDto has no validation annotations
        CompanyDto dtoWithNullName = CompanyDto.builder()
                .name(null)  // This is allowed at DTO level
                .countryCode("USA")
                .build();

        CompanyDto dtoWithEmptyName = CompanyDto.builder()
                .name("")    // This is allowed at DTO level
                .countryCode("USA")
                .build();

        CompanyDto dtoWithValidName = CompanyDto.builder()
                .name("Valid Company")  // This is allowed at DTO level
                .countryCode("USA")
                .build();

        // Then: All DTOs can be created without validation errors
        assertNotNull(dtoWithNullName);
        assertNull(dtoWithNullName.getName());

        assertNotNull(dtoWithEmptyName);
        assertEquals("", dtoWithEmptyName.getName());

        assertNotNull(dtoWithValidName);
        assertEquals("Valid Company", dtoWithValidName.getName());
    }
} 