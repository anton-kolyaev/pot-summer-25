package com.coherentsolutions.pot.insuranceservice.unit;

import com.coherentsolutions.pot.insuranceservice.dto.company.CompanyDto;
import com.coherentsolutions.pot.insuranceservice.enums.CompanyStatus;
import com.coherentsolutions.pot.insuranceservice.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Base abstract test class for controller unit tests. Provides common setup for MockMvc,
 * ObjectMapper, and test data factories.
 */
public abstract class AbstractControllerTest {

  protected static MockMvc mockMvc;
  protected static ObjectMapper objectMapper;

  /**
   * Initializes shared MockMvc and ObjectMapper for controller tests.
   *
   * @param controller the controller under test
   */
  protected static void initializeCommonObjects(Object controller) {
    mockMvc = MockMvcBuilders.standaloneSetup(controller)
        .setControllerAdvice(new GlobalExceptionHandler())
        .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
        .build();

    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
  }

  /**
   * Resets the provided Mockito mocks.
   *
   * @param mocks the mocks to reset
   */
  protected static void resetMocks(Object... mocks) {
    for (Object mock : mocks) {
      if (mock != null) {
        org.mockito.Mockito.reset(mock);
      }
    }
  }

  /**
   * Returns the shared MockMvc instance.
   *
   * @return the MockMvc instance
   */
  protected MockMvc getMockMvc() {
    return mockMvc;
  }

  /**
   * Returns the shared ObjectMapper instance.
   *
   * @return the ObjectMapper instance
   */
  protected ObjectMapper getObjectMapper() {
    return objectMapper;
  }

  /**
   * Creates a test CompanyDto with random UUID.
   *
   * @return a CompanyDto object
   */
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

  /**
   * Creates a test CompanyDto with a specific UUID.
   *
   * @param id the UUID to use for the company
   * @return a CompanyDto object
   */
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

  /**
   * Generates a random UUID to be used as a test company ID.
   *
   * @return a random UUID
   */
  protected UUID createTestCompanyId() {
    return UUID.randomUUID();
  }
} 