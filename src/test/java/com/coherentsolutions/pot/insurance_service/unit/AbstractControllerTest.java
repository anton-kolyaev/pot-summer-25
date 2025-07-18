package com.coherentsolutions.pot.insurance_service.unit;

import com.coherentsolutions.pot.insurance_service.dto.CompanyDto;
import com.coherentsolutions.pot.insurance_service.enums.CompanyStatus;
import com.coherentsolutions.pot.insurance_service.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.UUID;

public abstract class AbstractControllerTest {

    protected static MockMvc mockMvc;
    protected static ObjectMapper objectMapper;

    protected static void initializeCommonObjects(Object controller) {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
        
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    // Method to reset mocks - can be called from test methods
    protected static void resetMocks(Object... mocks) {
        for (Object mock : mocks) {
            if (mock != null) {
                org.mockito.Mockito.reset(mock);
            }
        }
    }

    protected MockMvc getMockMvc() {
        return mockMvc;
    }

    protected ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    // Factory methods for fresh objects - shared across all test classes
    protected CompanyDto createTestCompanyDto() {
        return CompanyDto.builder()
                .id(UUID.randomUUID())
                .name("Test Company")
                .status(CompanyStatus.ACTIVE)
                .countryCode("USA")
                .email("test@company.com")
                .website("https://testcompany.com")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    protected CompanyDto createTestCompanyDto(UUID id) {
        return CompanyDto.builder()
                .id(id)
                .name("Test Company")
                .status(CompanyStatus.ACTIVE)
                .countryCode("USA")
                .email("test@company.com")
                .website("https://testcompany.com")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    protected UUID createTestCompanyId() {
        return UUID.randomUUID();
    }
} 