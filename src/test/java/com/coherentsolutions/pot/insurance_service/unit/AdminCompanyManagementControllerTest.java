package com.coherentsolutions.pot.insurance_service.unit;

import com.coherentsolutions.pot.insurance_service.controller.AdminCompanyManagementController;
import com.coherentsolutions.pot.insurance_service.dto.CompanyDto;
import com.coherentsolutions.pot.insurance_service.service.CompanyManagementService;
import com.coherentsolutions.pot.insurance_service.service.UserManagementService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Admin Company Management Controller Tests")
class AdminCompanyManagementControllerTest extends AbstractControllerTest {

    private static CompanyManagementService companyManagementService;
    private static UserManagementService userManagementService;
    private static AdminCompanyManagementController controller;

    @BeforeAll
    static void setUpClass() {
        companyManagementService = mock(CompanyManagementService.class);
        userManagementService = mock(UserManagementService.class);
        controller = new AdminCompanyManagementController(companyManagementService, userManagementService);
        initializeCommonObjects(controller);
    }
    
    @Test
    @DisplayName("Should handle invalid UUID format in path parameter")
    void shouldHandleInvalidUuidFormatInPathParameter() throws Exception {
        // When & Then
        int status = getMockMvc().perform(get("/v1/companies/{id}", "invalid-uuid")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getStatus();
        assertEquals(400, status);

        // Create a simple DTO without Instant fields to avoid serialization issues
        CompanyDto simpleDto = CompanyDto.builder()
                .name("Test Company")
                .countryCode("USA")
                .build();

        status = getMockMvc().perform(put("/v1/companies/{id}", "invalid-uuid")
                        .content(getObjectMapper().writeValueAsString(simpleDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getStatus();
        assertEquals(400, status);
    }

    @Test
    @DisplayName("Should handle malformed pagination parameters")
    void shouldHandleMalformedPaginationParameters() throws Exception {
        // When & Then - Spring Boot handles malformed pagination gracefully, so we expect 200
        int status = getMockMvc().perform(get("/v1/companies")
                        .param("page", "invalid")
                        .param("size", "invalid")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getStatus();
        assertEquals(200, status);
    }

    @Test
    @DisplayName("Should handle negative pagination parameters")
    void shouldHandleNegativePaginationParameters() throws Exception {
        // When & Then - Spring Boot handles negative pagination gracefully, so we expect 200
        int status = getMockMvc().perform(get("/v1/companies")
                        .param("page", "-1")
                        .param("size", "-10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getStatus();
        assertEquals(200, status);
    }

    @Test
    @DisplayName("Should handle extremely large pagination parameters")
    void shouldHandleExtremelyLargePaginationParameters() throws Exception {
        // When & Then - Spring Boot handles large pagination gracefully, so we expect 200
        int status = getMockMvc().perform(get("/v1/companies")
                        .param("page", "999999999")
                        .param("size", "999999999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getStatus();
        assertEquals(200, status);
    }

    @Test
    @DisplayName("Should handle unsupported HTTP methods")
    void shouldHandleUnsupportedHttpMethods() throws Exception {
        // When & Then
        UUID testCompanyId = createTestCompanyId();
        int status = getMockMvc().perform(patch("/v1/companies/{id}", testCompanyId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getStatus();
        assertEquals(405, status); // Method Not Allowed
    }

    @Test
    @DisplayName("Should handle missing content type header")
    void shouldHandleMissingContentTypeHeader() throws Exception {
        // Given
        CompanyDto createRequest = CompanyDto.builder()
                .name("New Company")
                .countryCode("USA")
                .build();

        // When & Then
        int status = getMockMvc().perform(post("/v1/companies")
                        .content(getObjectMapper().writeValueAsString(createRequest)))
                .andReturn().getResponse().getStatus();
        assertEquals(415, status); // Unsupported Media Type

        UUID testCompanyId = createTestCompanyId();
        status = getMockMvc().perform(put("/v1/companies/{id}", testCompanyId)
                        .content(getObjectMapper().writeValueAsString(createRequest)))
                .andReturn().getResponse().getStatus();
        assertEquals(415, status); // Unsupported Media Type
    }

    @Test
    @DisplayName("Should handle unsupported content type")
    void shouldHandleUnsupportedContentType() throws Exception {
        // Given
        CompanyDto createRequest = CompanyDto.builder()
                .name("New Company")
                .countryCode("USA")
                .build();

        // When & Then
        int status = getMockMvc().perform(post("/v1/companies")
                        .content(getObjectMapper().writeValueAsString(createRequest))
                        .contentType(MediaType.TEXT_PLAIN))
                .andReturn().getResponse().getStatus();
        assertEquals(415, status); // Unsupported Media Type

        UUID testCompanyId = createTestCompanyId();
        status = getMockMvc().perform(put("/v1/companies/{id}", testCompanyId)
                        .content(getObjectMapper().writeValueAsString(createRequest))
                        .contentType(MediaType.TEXT_PLAIN))
                .andReturn().getResponse().getStatus();
        assertEquals(415, status); // Unsupported Media Type
    }
} 