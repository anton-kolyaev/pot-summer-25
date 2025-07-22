package com.coherentsolutions.pot.insuranceservice.unit.dto.user;

import com.coherentsolutions.pot.insuranceservice.dto.user.UserFilter;
import com.coherentsolutions.pot.insuranceservice.enums.UserFunction;
import com.coherentsolutions.pot.insuranceservice.enums.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Filter Tests")
class UserFilterTest {

    private UserFilter filter;

    private static final String NAME_OLD = "Old Name";
    private static final String NAME_NEW = "New Name";
    private static final String EMAIL_OLD = "old@example.com";
    private static final String EMAIL_NEW = "new@example.com";
    private static final String SSN_OLD = "000-00-0000";
    private static final String SSN_NEW = "999-99-9999";
    private static final LocalDate DOB_OLD = LocalDate.of(1980, 1, 1);
    private static final LocalDate DOB_NEW = LocalDate.of(1995, 12, 31);

    @BeforeEach
    void setUp() {
        filter = new UserFilter();
    }

    @Test
    @DisplayName("Should create empty filter")
    void shouldCreateEmptyFilter() {
        assertNull(filter.getName());
        assertNull(filter.getEmail());
        assertNull(filter.getDateOfBirth());
        assertNull(filter.getStatus());
        assertNull(filter.getSsn());
        assertNull(filter.getFunctions());
    }

    @Test
    @DisplayName("Should set and get name")
    void shouldSetAndGetName() {
        filter.setName(NAME_NEW);
        assertEquals(NAME_NEW, filter.getName());
    }

    @Test
    @DisplayName("Should set and get email")
    void shouldSetAndGetEmail() {
        filter.setEmail(EMAIL_NEW);
        assertEquals(EMAIL_NEW, filter.getEmail());
    }

    @Test
    @DisplayName("Should set and get date of birth")
    void shouldSetAndGetDateOfBirth() {
        filter.setDateOfBirth(DOB_NEW);
        assertEquals(DOB_NEW, filter.getDateOfBirth());
    }

    @Test
    @DisplayName("Should set and get user status")
    void shouldSetAndGetUserStatus() {
        filter.setStatus(UserStatus.ACTIVE);
        assertEquals(UserStatus.ACTIVE, filter.getStatus());

        filter.setStatus(UserStatus.INACTIVE);
        assertEquals(UserStatus.INACTIVE, filter.getStatus());
    }

    @Test
    @DisplayName("Should set and get SSN")
    void shouldSetAndGetSSN() {
        filter.setSsn(SSN_NEW);
        assertEquals(SSN_NEW, filter.getSsn());
    }

    @Test
    @DisplayName("Should set and get user functions")
    void shouldSetAndGetFunctions() {
        Set<UserFunction> functions = EnumSet.of(UserFunction.CONSUMER_CLAIM_MANAGER, UserFunction.COMPANY_MANAGER);
        filter.setFunctions(functions);
        assertEquals(2, filter.getFunctions().size());
        assertTrue(filter.getFunctions().contains(UserFunction.CONSUMER_CLAIM_MANAGER));
        assertTrue(filter.getFunctions().contains(UserFunction.COMPANY_MANAGER));
    }

    @Test
    @DisplayName("Should handle null and empty values")
    void shouldHandleNullAndEmptyValues() {
        filter.setName(null);
        filter.setEmail(null);
        filter.setDateOfBirth(null);
        filter.setStatus(null);
        filter.setSsn(null);
        filter.setFunctions(null);

        assertNull(filter.getName());
        assertNull(filter.getEmail());
        assertNull(filter.getDateOfBirth());
        assertNull(filter.getStatus());
        assertNull(filter.getSsn());
        assertNull(filter.getFunctions());
    }

    @Test
    @DisplayName("Should update filter values")
    void shouldUpdateFilterValues() {
        filter.setName(NAME_OLD);
        filter.setEmail(EMAIL_OLD);
        filter.setDateOfBirth(DOB_OLD);
        filter.setStatus(UserStatus.INACTIVE);
        filter.setSsn(SSN_OLD);
        filter.setFunctions(EnumSet.of(UserFunction.COMPANY_SETTING_MANAGER));

        filter.setName(NAME_NEW);
        filter.setEmail(EMAIL_NEW);
        filter.setDateOfBirth(DOB_NEW);
        filter.setStatus(UserStatus.ACTIVE);
        filter.setSsn(SSN_NEW);
        filter.setFunctions(EnumSet.of(UserFunction.COMPANY_SETTING_MANAGER, UserFunction.CONSUMER));

        assertEquals(NAME_NEW, filter.getName());
        assertEquals(EMAIL_NEW, filter.getEmail());
        assertEquals(DOB_NEW, filter.getDateOfBirth());
        assertEquals(UserStatus.ACTIVE, filter.getStatus());
        assertEquals(SSN_NEW, filter.getSsn());
        assertEquals(2, filter.getFunctions().size());
        assertTrue(filter.getFunctions().contains(UserFunction.COMPANY_SETTING_MANAGER));
        assertTrue(filter.getFunctions().contains(UserFunction.CONSUMER));
    }
}
