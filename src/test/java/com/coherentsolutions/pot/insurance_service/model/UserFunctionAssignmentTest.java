package com.coherentsolutions.pot.insurance_service.model;

import com.coherentsolutions.pot.insurance_service.enums.UserFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserFunctionAssignment Entity Tests")
class UserFunctionAssignmentTest {

    private UserFunctionAssignment assignment;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("johndoe");
        user.setEmail("john.doe@example.com");
        user.setSsn("123-45-6789");
        user.setDateOfBirth(java.time.LocalDate.of(1990, 1, 1));

        assignment = new UserFunctionAssignment();
        assignment.setId(UUID.randomUUID());
        assignment.setFunction(UserFunction.COMPANY_REPORT_MANAGER);
        assignment.setUser(user);
    }

    @Test
    @DisplayName("Should create assignment with all required fields")
    void shouldCreateAssignmentWithAllRequiredFields() {
        assertThat(assignment).isNotNull();
        assertThat(assignment.getId()).isNotNull();
        assertThat(assignment.getFunction()).isEqualTo(UserFunction.COMPANY_REPORT_MANAGER);
        assertThat(assignment.getUser()).isEqualTo(user);
    }

    @Test
    @DisplayName("Should update assignment fields")
    void shouldUpdateAssignmentFields() {
        // Given
        User newUser = new User();
        newUser.setId(UUID.randomUUID());
        newUser.setFirstName("Jane");
        newUser.setLastName("Smith");
        newUser.setUsername("janesmith");
        newUser.setEmail("jane.smith@example.com");
        newUser.setSsn("987-65-4321");
        newUser.setDateOfBirth(java.time.LocalDate.of(1985, 5, 15));

        // When
        assignment.setFunction(UserFunction.CONSUMER);
        assignment.setUser(newUser);

        // Then
        assertThat(assignment.getFunction()).isEqualTo(UserFunction.CONSUMER);
        assertThat(assignment.getUser()).isEqualTo(newUser);
    }

    @Test
    @DisplayName("Should handle null user reference")
    void shouldHandleNullUserReference() {
        // When
        assignment.setUser(null);

        // Then
        assertThat(assignment.getUser()).isNull();
    }

    @Test
    @DisplayName("Should handle different UserFunction enum values")
    void shouldHandleDifferentUserFunctionValues() {
        // When
        assignment.setFunction(UserFunction.COMPANY_REPORT_MANAGER);
        assertThat(assignment.getFunction()).isEqualTo(UserFunction.COMPANY_REPORT_MANAGER);

        assignment.setFunction(UserFunction.CONSUMER);
        assertThat(assignment.getFunction()).isEqualTo(UserFunction.CONSUMER);

        assignment.setFunction(UserFunction.CONSUMER_CLAIM_MANAGER);
        assertThat(assignment.getFunction()).isEqualTo(UserFunction.CONSUMER_CLAIM_MANAGER);
    }

}
