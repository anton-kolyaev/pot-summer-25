package com.coherentsolutions.pot.insurance_service.unit.dto;

import com.coherentsolutions.pot.insurance_service.dto.CompanyFilter;
import com.coherentsolutions.pot.insurance_service.enums.CompanyStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Company Filter Tests")
class CompanyFilterTest {

    private CompanyFilter filter;

    @BeforeEach
    void setUp() {
        filter = new CompanyFilter();
    }

    @Test
    @DisplayName("Should create empty filter")
    void shouldCreateEmptyFilter() {
        // Then
        assertNull(filter.getName());
        assertNull(filter.getCountryCode());
        assertNull(filter.getStatus());
        assertNull(filter.getCreatedFrom());
        assertNull(filter.getCreatedTo());
        assertNull(filter.getUpdatedFrom());
        assertNull(filter.getUpdatedTo());
    }

    @Test
    @DisplayName("Should set and get name filter")
    void shouldSetAndGetNameFilter() {
        // Given
        String name = "Test Company";

        // When
        filter.setName(name);

        // Then
        assertEquals(name, filter.getName());
    }

    @Test
    @DisplayName("Should set and get country code filter")
    void shouldSetAndGetCountryCodeFilter() {
        // Given
        String countryCode = "USA";

        // When
        filter.setCountryCode(countryCode);

        // Then
        assertEquals(countryCode, filter.getCountryCode());
    }

    @Test
    @DisplayName("Should set and get status filter")
    void shouldSetAndGetStatusFilter() {
        // Given
        CompanyStatus status = CompanyStatus.ACTIVE;

        // When
        filter.setStatus(status);

        // Then
        assertEquals(status, filter.getStatus());
    }

    @Test
    @DisplayName("Should set and get date range filters")
    void shouldSetAndGetDateRangeFilters() {
        // Given
        Instant createdFrom = Instant.now().minusSeconds(3600);
        Instant createdTo = Instant.now();
        Instant updatedFrom = Instant.now().minusSeconds(1800);
        Instant updatedTo = Instant.now();

        // When
        filter.setCreatedFrom(createdFrom);
        filter.setCreatedTo(createdTo);
        filter.setUpdatedFrom(updatedFrom);
        filter.setUpdatedTo(updatedTo);

        // Then
        assertEquals(createdFrom, filter.getCreatedFrom());
        assertEquals(createdTo, filter.getCreatedTo());
        assertEquals(updatedFrom, filter.getUpdatedFrom());
        assertEquals(updatedTo, filter.getUpdatedTo());
    }

    @Test
    @DisplayName("Should handle null values")
    void shouldHandleNullValues() {
        // When
        filter.setName(null);
        filter.setCountryCode(null);
        filter.setStatus(null);
        filter.setCreatedFrom(null);
        filter.setCreatedTo(null);
        filter.setUpdatedFrom(null);
        filter.setUpdatedTo(null);

        // Then
        assertNull(filter.getName());
        assertNull(filter.getCountryCode());
        assertNull(filter.getStatus());
        assertNull(filter.getCreatedFrom());
        assertNull(filter.getCreatedTo());
        assertNull(filter.getUpdatedFrom());
        assertNull(filter.getUpdatedTo());
    }

    @Test
    @DisplayName("Should handle different country codes")
    void shouldHandleDifferentCountryCodes() {
        // When
        filter.setCountryCode("USA");
        assertEquals("USA", filter.getCountryCode());

        filter.setCountryCode("CAN");
        assertEquals("CAN", filter.getCountryCode());

        filter.setCountryCode("GBR");
        assertEquals("GBR", filter.getCountryCode());
    }

    @Test
    @DisplayName("Should handle different company statuses")
    void shouldHandleDifferentCompanyStatuses() {
        // When
        filter.setStatus(CompanyStatus.ACTIVE);
        assertEquals(CompanyStatus.ACTIVE, filter.getStatus());

        filter.setStatus(CompanyStatus.DEACTIVATED);
        assertEquals(CompanyStatus.DEACTIVATED, filter.getStatus());
    }

    @Test
    @DisplayName("Should handle empty string values")
    void shouldHandleEmptyStringValues() {
        // When
        filter.setName("");
        filter.setCountryCode("");

        // Then
        assertTrue(filter.getName().isEmpty());
        assertTrue(filter.getCountryCode().isEmpty());
    }

    @Test
    @DisplayName("Should handle whitespace values")
    void shouldHandleWhitespaceValues() {
        // When
        filter.setName("  ");
        filter.setCountryCode("  ");

        // Then
        assertEquals("  ", filter.getName());
        assertEquals("  ", filter.getCountryCode());
    }

    @Test
    @DisplayName("Should handle case sensitive values")
    void shouldHandleCaseSensitiveValues() {
        // When
        filter.setName("Test Company");
        filter.setCountryCode("usa");

        // Then
        assertEquals("Test Company", filter.getName());
        assertEquals("usa", filter.getCountryCode());
    }

    @Test
    @DisplayName("Should update filter values")
    void shouldUpdateFilterValues() {
        // Given
        String initialName = "Initial Company";
        String updatedName = "Updated Company";
        String initialCountryCode = "USA";
        String updatedCountryCode = "CAN";

        // When
        filter.setName(initialName);
        filter.setCountryCode(initialCountryCode);

        // Then
        assertEquals(initialName, filter.getName());
        assertEquals(initialCountryCode, filter.getCountryCode());

        // When
        filter.setName(updatedName);
        filter.setCountryCode(updatedCountryCode);

        // Then
        assertEquals(updatedName, filter.getName());
        assertEquals(updatedCountryCode, filter.getCountryCode());
    }

    @Test
    @DisplayName("Should handle complex search scenarios")
    void shouldHandleComplexSearchScenarios() {
        // Given
        String companyName = "Test Company";
        String countryCode = "USA";
        CompanyStatus status = CompanyStatus.ACTIVE;
        Instant createdFrom = Instant.now().minusSeconds(7200);
        Instant createdTo = Instant.now();

        // When
        filter.setName(companyName);
        filter.setCountryCode(countryCode);
        filter.setStatus(status);
        filter.setCreatedFrom(createdFrom);
        filter.setCreatedTo(createdTo);

        // Then
        assertEquals(companyName, filter.getName());
        assertEquals(countryCode, filter.getCountryCode());
        assertEquals(status, filter.getStatus());
        assertEquals(createdFrom, filter.getCreatedFrom());
        assertEquals(createdTo, filter.getCreatedTo());
    }
} 