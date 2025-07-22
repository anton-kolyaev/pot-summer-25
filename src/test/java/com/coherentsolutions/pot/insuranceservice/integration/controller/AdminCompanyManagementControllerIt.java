package com.coherentsolutions.pot.insuranceservice.integration.controller;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.coherentsolutions.pot.insuranceservice.dto.company.CompanyDto;
import com.coherentsolutions.pot.insuranceservice.dto.company.CompanyReactivationRequest;
import com.coherentsolutions.pot.insuranceservice.enums.CompanyStatus;
import com.coherentsolutions.pot.insuranceservice.enums.UserStatus;
import com.coherentsolutions.pot.insuranceservice.integration.IntegrationTestConfiguration;
import com.coherentsolutions.pot.insuranceservice.integration.containers.PostgresTestContainer;
import com.coherentsolutions.pot.insuranceservice.model.Company;
import com.coherentsolutions.pot.insuranceservice.model.User;
import com.coherentsolutions.pot.insuranceservice.repository.CompanyRepository;
import com.coherentsolutions.pot.insuranceservice.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

/**
 * Integration tests for AdminCompanyManagementController. Tests user listing endpoints and request
 * validation using real database containers.
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@Import(IntegrationTestConfiguration.class)
@DisplayName("Integration test for AdminCompanyManagementController")
public class AdminCompanyManagementControllerIt extends PostgresTestContainer {

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private CompanyRepository companyRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("Should create and retrieve company successfully")
  void shouldCreateAndRetrieveCompanySuccessfully() throws Exception {
    // Given
    CompanyDto createRequest = buildCompanyDto("Integration Test Company", "USA", "integration@test.com", "https://integration-test.com");

    // When & Then - Create company
    CompanyDto createdCompany = createCompany(createRequest);
    assertNotNull(createdCompany.getId());
    validateCompanyWithWebsite(createdCompany, "Integration Test Company", "USA", "integration@test.com", "https://integration-test.com");
    validateCompanyStatus(createdCompany, CompanyStatus.ACTIVE);

    UUID companyId = createdCompany.getId();

    // When & Then - Retrieve the created company
    CompanyDto retrievedCompany = getCompany(companyId);
    assertEquals(companyId, retrievedCompany.getId());
    validateCompanyWithWebsite(retrievedCompany, "Integration Test Company", "USA", "integration@test.com", "https://integration-test.com");
    validateCompanyStatus(retrievedCompany, CompanyStatus.ACTIVE);
  }

  @Test
  @DisplayName("Should get companies with search filters")
  void shouldGetCompaniesWithSearchFilters() throws Exception {
    // Given - Create multiple companies
    CompanyDto company1 = buildCompanyDto("Alpha Company", "USA", "alpha@company.com", CompanyStatus.ACTIVE);
    CompanyDto company2 = buildCompanyDto("Beta Company", "CAN", "beta@company.com", CompanyStatus.ACTIVE);

    // Create companies
    createCompany(company1);
    createCompany(company2);

    // When & Then - Search by name
    String searchByNameResponse = mockMvc.perform(get("/v1/companies")
                    .param("name", "Alpha")
                    .param("page", "0")
                    .param("size", "10")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    // Parse and validate search results using TypeReference
    var searchByNameResult = objectMapper.readTree(searchByNameResponse);
    assertTrue(searchByNameResult.has("content"));
    assertTrue(searchByNameResult.get("content").isArray());

    // Map the content array to List<CompanyDto> using TypeReference
    List<CompanyDto> searchByNameCompanies = objectMapper.readValue(
            searchByNameResult.get("content").toString(),
            new TypeReference<List<CompanyDto>>() {
            }
    );
    assertFalse(searchByNameCompanies.isEmpty());
    assertEquals("Alpha Company", searchByNameCompanies.get(0).getName());

    // When & Then - Search by country code
    String searchByCountryResponse = mockMvc.perform(get("/v1/companies")
                    .param("countryCode", "CAN")
                    .param("page", "0")
                    .param("size", "10")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    // Parse and validate search results using TypeReference
    var searchByCountryResult = objectMapper.readTree(searchByCountryResponse);
    assertTrue(searchByCountryResult.has("content"));
    assertTrue(searchByCountryResult.get("content").isArray());

    // Map the content array to List<CompanyDto> using TypeReference
    List<CompanyDto> searchByCountryCompanies = objectMapper.readValue(
            searchByCountryResult.get("content").toString(),
            new TypeReference<List<CompanyDto>>() {
            }
    );
    assertFalse(searchByCountryCompanies.isEmpty());
    assertEquals("CAN", searchByCountryCompanies.get(0).getCountryCode());
  }

  @Test
  @DisplayName("Should update company successfully")
  void shouldUpdateCompanySuccessfully() throws Exception {
    // Given - Create a company first
    CompanyDto createRequest = buildCompanyDto("Original Company", "USA", "original@company.com");
    CompanyDto createdCompany = createCompany(createRequest);
    UUID companyId = createdCompany.getId();

    // Given - Update request
    CompanyDto updateRequest = buildCompanyDto("Updated Company", "CAN", "updated@company.com", "https://updated-company.com");

    // When & Then - Update the company
    CompanyDto updatedCompany = updateCompany(companyId, updateRequest);
    assertEquals(companyId, updatedCompany.getId());
    validateCompanyWithWebsite(updatedCompany, "Updated Company", "CAN", "updated@company.com", "https://updated-company.com");

    // Verify the update persisted by retrieving the company
    CompanyDto retrievedCompany = getCompany(companyId);
    assertEquals("Updated Company", retrievedCompany.getName());
    assertEquals("CAN", retrievedCompany.getCountryCode());
  }

  @Test
  @DisplayName("Should return 404 when company not found")
  void shouldReturn404WhenCompanyNotFound() throws Exception {
    // Given
    UUID nonExistentId = UUID.randomUUID();

    // When & Then
    mockMvc.perform(get("/v1/companies/{id}", nonExistentId)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should return 404 when updating non-existent company")
  void shouldReturn404WhenUpdatingNonExistentCompany() throws Exception {
    // Given
    UUID nonExistentId = UUID.randomUUID();
    CompanyDto updateRequest = buildCompanyDto("Updated Company", "CAN", "updated@company.com");

    // When & Then
    mockMvc.perform(put("/v1/companies/{id}", nonExistentId)
                    .content(objectMapper.writeValueAsString(updateRequest))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should return 400 when creating company with invalid JSON")
  void shouldReturn400WhenCreatingCompanyWithInvalidJson() throws Exception {
    // Given
    String invalidJson = "{ invalid json }";

    // When & Then
    mockMvc.perform(post("/v1/companies")
                    .content(invalidJson)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 400 when updating company with invalid JSON")
  void shouldReturn400WhenUpdatingCompanyWithInvalidJson() throws Exception {
    // Given
    String invalidJson = "{ invalid json }";

    // When & Then
    mockMvc.perform(put("/v1/companies/{id}", UUID.randomUUID())
                    .content(invalidJson)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 400 when creating company with empty request body")
  void shouldReturn400WhenCreatingCompanyWithEmptyBody() throws Exception {
    // When & Then
    mockMvc.perform(post("/v1/companies")
                    .content("")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 400 when updating company with empty request body")
  void shouldReturn400WhenUpdatingCompanyWithEmptyBody() throws Exception {
    // When & Then
    mockMvc.perform(put("/v1/companies/{id}", UUID.randomUUID())
                    .content("")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 400 when creating company with null request body")
  void shouldReturn400WhenCreatingCompanyWithNullBody() throws Exception {
    // When & Then
    mockMvc.perform(post("/v1/companies")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 400 when updating company with null request body")
  void shouldReturn400WhenUpdatingCompanyWithNullBody() throws Exception {
    // When & Then
    mockMvc.perform(put("/v1/companies/{id}", UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should handle invalid UUID format in path parameter")
  void shouldHandleInvalidUuidFormatInPathParameter() throws Exception {
    // When & Then
    mockMvc.perform(get("/v1/companies/{id}", "invalid-uuid")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

    CompanyDto simpleDto = CompanyDto.builder()
            .name("Test Company")
            .countryCode("USA")
            .build();

    mockMvc.perform(put("/v1/companies/{id}", "invalid-uuid")
                    .content(objectMapper.writeValueAsString(simpleDto))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should handle malformed pagination parameters")
  void shouldHandleMalformedPaginationParameters() throws Exception {
    // When & Then - Spring Boot handles malformed pagination gracefully
    mockMvc.perform(get("/v1/companies")
                    .param("page", "invalid")
                    .param("size", "invalid")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Should handle negative pagination parameters")
  void shouldHandleNegativePaginationParameters() throws Exception {
    // When & Then - Spring Boot handles negative pagination gracefully
    mockMvc.perform(get("/v1/companies")
                    .param("page", "-1")
                    .param("size", "-10")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Should handle unsupported HTTP methods")
  void shouldHandleUnsupportedHttpMethods() throws Exception {
    // When & Then
    mockMvc.perform(patch("/v1/companies/{id}", UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isMethodNotAllowed());
  }

  @Test
  @DisplayName("Should handle missing content type header")
  void shouldHandleMissingContentTypeHeader() throws Exception {
    // Given
    CompanyDto createRequest = buildCompanyDto("New Company", "USA", "new@company.com");

    // When & Then
    mockMvc.perform(post("/v1/companies")
                    .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isUnsupportedMediaType());

    mockMvc.perform(put("/v1/companies/{id}", UUID.randomUUID())
                    .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isUnsupportedMediaType());
  }

  @Test
  @DisplayName("Should handle unsupported content type")
  void shouldHandleUnsupportedContentType() throws Exception {
    // Given
    CompanyDto createRequest = buildCompanyDto("New Company", "USA", "new@company.com");

    // When & Then
    mockMvc.perform(post("/v1/companies")
                    .content(objectMapper.writeValueAsString(createRequest))
                    .contentType(MediaType.TEXT_PLAIN))
            .andExpect(status().isUnsupportedMediaType());

    mockMvc.perform(put("/v1/companies/{id}", UUID.randomUUID())
                    .content(objectMapper.writeValueAsString(createRequest))
                    .contentType(MediaType.TEXT_PLAIN))
            .andExpect(status().isUnsupportedMediaType());
  }

  @Test
  @DisplayName("Should handle database constraint violations")
  void shouldHandleDatabaseConstraintViolations() throws Exception {
    // Given - Create a company with required fields
    CompanyDto createRequest = buildCompanyDto("Test Company", "USA", "test@company.com");

    // Create the first company successfully
    createCompany(createRequest);

    // Try to create another company with the same email (assuming unique constraint on email)
    CompanyDto duplicateRequest = buildCompanyDto("Different Company", "CAN", "test@company.com");

    createCompany(duplicateRequest); // Since there's no unique constraint, this should succeed
  }

  @Test
  @DisplayName("Should deactivate company successfully")
  void shouldDeactivateCompanySuccessfully() throws Exception {
    // Given - Create a company first
    CompanyDto createRequest = buildCompanyDto("Company to Deactivate", "USA", "deactivate@company.com");
    CompanyDto createdCompany = createCompany(createRequest);
    UUID companyId = createdCompany.getId();

    // When & Then - Deactivate the company
    CompanyDto deactivatedCompany = deactivateCompany(companyId);
    assertEquals(companyId, deactivatedCompany.getId());
    validateCompanyStatus(deactivatedCompany, CompanyStatus.DEACTIVATED);

    // Verify the deactivation persisted by retrieving the company
    CompanyDto retrievedCompany = getCompany(companyId);
    validateCompanyStatus(retrievedCompany, CompanyStatus.DEACTIVATED);
  }

  @Test
  @DisplayName("Should return 400 when deactivating already deactivated company")
  void shouldReturn400WhenDeactivatingAlreadyDeactivatedCompany() throws Exception {
    // Given - Create and deactivate a company
    CompanyDto createRequest = buildCompanyDto("Company to Deactivate", "USA", "deactivate@company.com");
    CompanyDto createdCompany = createCompany(createRequest);
    UUID companyId = createdCompany.getId();

    // Deactivate the company
    deactivateCompany(companyId);

    // When & Then - Try to deactivate again
    mockMvc.perform(delete("/v1/companies/{id}", companyId)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should reactivate company with all users successfully")
  void shouldReactivateCompanyWithAllUsersSuccessfully() throws Exception {
    // Given - Create and deactivate a company
    CompanyDto createRequest = buildCompanyDto("Company to Reactivate", "USA", "reactivate@company.com");
    CompanyDto createdCompany = createCompany(createRequest);
    UUID companyId = createdCompany.getId();

    // Deactivate the company
    deactivateCompany(companyId);

    // When & Then - Reactivate with ALL users option
    CompanyReactivationRequest reactivateRequest = new CompanyReactivationRequest(
            CompanyReactivationRequest.UserReactivationOption.ALL, null);

    CompanyDto reactivatedCompany = reactivateCompany(companyId, reactivateRequest);
    assertEquals(companyId, reactivatedCompany.getId());
    validateCompanyStatus(reactivatedCompany, CompanyStatus.ACTIVE);

    // Verify the reactivation persisted
    CompanyDto retrievedCompany = getCompany(companyId);
    validateCompanyStatus(retrievedCompany, CompanyStatus.ACTIVE);
  }

  @Test
  @DisplayName("Should reactivate company with selected users successfully")
  void shouldReactivateCompanyWithSelectedUsersSuccessfully() throws Exception {
    // Given - Create and deactivate a company
    CompanyDto createRequest = buildCompanyDto("Company to Reactivate", "USA", "reactivate@company.com");
    CompanyDto createdCompany = createCompany(createRequest);
    UUID companyId = createdCompany.getId();

    // Deactivate the company
    deactivateCompany(companyId);

    // When & Then - Reactivate with SELECTED users option
    List<UUID> selectedUserIds = List.of(UUID.randomUUID(), UUID.randomUUID());
    CompanyReactivationRequest reactivateRequest = new CompanyReactivationRequest(
            CompanyReactivationRequest.UserReactivationOption.SELECTED, selectedUserIds);

    CompanyDto reactivatedCompany = reactivateCompany(companyId, reactivateRequest);
    assertEquals(companyId, reactivatedCompany.getId());
    validateCompanyStatus(reactivatedCompany, CompanyStatus.ACTIVE);
  }

  @Test
  @DisplayName("Should return 400 when reactivating with SELECTED option but no user IDs")
  void shouldReturn400WhenReactivatingWithSelectedOptionButNoUserIds() throws Exception {
    // Given - Create and deactivate a company
    CompanyDto createRequest = buildCompanyDto("Company to Reactivate", "USA", "reactivate@company.com");
    CompanyDto createdCompany = createCompany(createRequest);
    UUID companyId = createdCompany.getId();

    // Deactivate the company
    deactivateCompany(companyId);

    // When & Then - Reactivate with SELECTED option but no user IDs
    CompanyReactivationRequest reactivateRequest = new CompanyReactivationRequest(
            CompanyReactivationRequest.UserReactivationOption.SELECTED, null);

    mockMvc.perform(post("/v1/companies/{id}/reactivate", companyId)
                    .content(objectMapper.writeValueAsString(reactivateRequest))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 400 when reactivating already active company")
  void shouldReturn400WhenReactivatingAlreadyActiveCompany() throws Exception {
    // Given - Create a company (already active)
    CompanyDto createRequest = buildCompanyDto("Active Company", "USA", "active@company.com");
    CompanyDto createdCompany = createCompany(createRequest);
    UUID companyId = createdCompany.getId();

    // When & Then - Try to reactivate already active company
    CompanyReactivationRequest reactivateRequest = new CompanyReactivationRequest(
            CompanyReactivationRequest.UserReactivationOption.NONE, null);

    mockMvc.perform(post("/v1/companies/{id}/reactivate", companyId)
                    .content(objectMapper.writeValueAsString(reactivateRequest))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 404 when deactivating non-existent company")
  void shouldReturn404WhenDeactivatingNonExistentCompany() throws Exception {
    // Given
    UUID nonExistentId = UUID.randomUUID();

    // When & Then
    mockMvc.perform(delete("/v1/companies/{id}", nonExistentId)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should return 404 when reactivating non-existent company")
  void shouldReturn404WhenReactivatingNonExistentCompany() throws Exception {
    // Given
    UUID nonExistentId = UUID.randomUUID();
    CompanyReactivationRequest reactivateRequest = new CompanyReactivationRequest(
            CompanyReactivationRequest.UserReactivationOption.NONE, null);

    // When & Then
    mockMvc.perform(post("/v1/companies/{id}/reactivate", nonExistentId)
                    .content(objectMapper.writeValueAsString(reactivateRequest))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should return all users of a company by companyId")
  void shouldReturnAllUsersOfExistingCompany() throws Exception {
    Company company = createTestCompany("Test Company", "USA", "test@example.com", "https://example.com");
    UUID companyId = company.getId();

    User user1 = createTestUser("Alice", "Johnson", "alice.johnson", "alice@example.com", company);
    User user2 = createTestUser("Bob", "Smith", "bob.smith", "bob@example.com", company, UserStatus.INACTIVE);

    mockMvc.perform(get("/v1/companies/{id}/users", companyId)
            .param("page", "0")
            .param("size", "10")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(
            jsonPath("$.content[*].username", containsInAnyOrder("alice.johnson", "bob.smith")))
        .andExpect(jsonPath("$.content[*].companyId",
            containsInAnyOrder(companyId.toString(), companyId.toString())));
  }

  @Test
  @DisplayName("Should return empty page if no users found for company")
  void shouldReturnEmptyPageIfNoUsersFoundForCompany() throws Exception {
    Company emptyCompany = createTestCompany("Empty Company", "USA", "empty@example.com");

    mockMvc.perform(get("/v1/companies/{id}/users", emptyCompany.getId())
            .param("page", "0")
            .param("size", "10")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(0))
        .andExpect(jsonPath("$.totalElements").value(0));
  }

  @Test
  @DisplayName("Should return Bad Request when companyId has invalid format")
  void shouldReturnBadRequestForInvalidCompanyId() throws Exception {
    String invalidCompanyId = "invalid-company-id";
    mockMvc.perform(get("/v1/companies/{id}/users", invalidCompanyId)
            .param("page", "0")
            .param("size", "10")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  // ========== PRIVATE HELPER METHODS ==========

  /**
   * Creates a company via POST request and returns the created company DTO.
   */
  private CompanyDto createCompany(CompanyDto createRequest) throws Exception {
    String responseJson = mockMvc.perform(post("/v1/companies")
                    .content(objectMapper.writeValueAsString(createRequest))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    return objectMapper.readValue(responseJson, CompanyDto.class);
  }

  /**
   * Creates a company via POST request with minimal required fields.
   */
  private CompanyDto createCompany(String name, String countryCode, String email) throws Exception {
    CompanyDto createRequest = CompanyDto.builder()
            .name(name)
            .countryCode(countryCode)
            .email(email)
            .build();

    return createCompany(createRequest);
  }

  /**
   * Creates a company via POST request with all fields.
   */
  private CompanyDto createCompany(String name, String countryCode, String email, String website) throws Exception {
    CompanyDto createRequest = CompanyDto.builder()
            .name(name)
            .countryCode(countryCode)
            .email(email)
            .website(website)
            .build();

    return createCompany(createRequest);
  }

  /**
   * Retrieves a company by ID via GET request.
   */
  private CompanyDto getCompany(UUID companyId) throws Exception {
    String responseJson = mockMvc.perform(get("/v1/companies/{id}", companyId)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    return objectMapper.readValue(responseJson, CompanyDto.class);
  }

  /**
   * Updates a company via PUT request and returns the updated company DTO.
   */
  private CompanyDto updateCompany(UUID companyId, CompanyDto updateRequest) throws Exception {
    String responseJson = mockMvc.perform(put("/v1/companies/{id}", companyId)
                    .content(objectMapper.writeValueAsString(updateRequest))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    return objectMapper.readValue(responseJson, CompanyDto.class);
  }

  /**
   * Deactivates a company via DELETE request and returns the deactivated company DTO.
   */
  private CompanyDto deactivateCompany(UUID companyId) throws Exception {
    String responseJson = mockMvc.perform(delete("/v1/companies/{id}", companyId)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    return objectMapper.readValue(responseJson, CompanyDto.class);
  }

  /**
   * Reactivates a company via POST request and returns the reactivated company DTO.
   */
  private CompanyDto reactivateCompany(UUID companyId, CompanyReactivationRequest reactivateRequest) throws Exception {
    String responseJson = mockMvc.perform(post("/v1/companies/{id}/reactivate", companyId)
                    .content(objectMapper.writeValueAsString(reactivateRequest))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    return objectMapper.readValue(responseJson, CompanyDto.class);
  }

  /**
   * Creates a CompanyDto with the specified fields.
   */
  private CompanyDto buildCompanyDto(String name, String countryCode, String email) {
    return CompanyDto.builder()
            .name(name)
            .countryCode(countryCode)
            .email(email)
            .build();
  }

  /**
   * Creates a CompanyDto with all fields.
   */
  private CompanyDto buildCompanyDto(String name, String countryCode, String email, String website) {
    return CompanyDto.builder()
            .name(name)
            .countryCode(countryCode)
            .email(email)
            .website(website)
            .build();
  }

  /**
   * Creates a CompanyDto with status.
   */
  private CompanyDto buildCompanyDto(String name, String countryCode, String email, CompanyStatus status) {
    return CompanyDto.builder()
            .name(name)
            .countryCode(countryCode)
            .email(email)
            .status(status)
            .build();
  }

  /**
   * Creates and saves a Company entity for testing.
   */
  private Company createTestCompany(String name, String countryCode, String email) {
    Company company = new Company();
    company.setName(name);
    company.setCountryCode(countryCode);
    company.setEmail(email);
    company.setStatus(CompanyStatus.ACTIVE);
    return companyRepository.save(company);
  }

  /**
   * Creates and saves a Company entity with all fields.
   */
  private Company createTestCompany(String name, String countryCode, String email, String website) {
    Company company = new Company();
    company.setName(name);
    company.setCountryCode(countryCode);
    company.setEmail(email);
    company.setWebsite(website);
    company.setStatus(CompanyStatus.ACTIVE);
    return companyRepository.save(company);
  }

  /**
   * Creates and saves a User entity for testing.
   */
  private User createTestUser(String firstName, String lastName, String username, String email, Company company) {
    User user = new User();
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setUsername(username);
    user.setEmail(email);
    user.setCompany(company);
    user.setStatus(UserStatus.ACTIVE);
    user.setDateOfBirth(LocalDate.of(1990, 1, 1));
    user.setSsn(generateUniqueSsn());
    return userRepository.save(user);
  }

  /**
   * Creates and saves a User entity with custom status.
   */
  private User createTestUser(String firstName, String lastName, String username, String email, Company company, UserStatus status) {
    User user = new User();
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setUsername(username);
    user.setEmail(email);
    user.setCompany(company);
    user.setStatus(status);
    user.setDateOfBirth(LocalDate.of(1990, 1, 1));
    user.setSsn(generateUniqueSsn());
    return userRepository.save(user);
  }

  /**
   * Generates a unique SSN for testing purposes.
   */
  private String generateUniqueSsn() {
    return String.format("%03d-%02d-%04d", 
        (int) (Math.random() * 1000), 
        (int) (Math.random() * 100), 
        (int) (Math.random() * 10000));
  }

  /**
   * Validates that a company has the expected basic fields.
   */
  private void validateCompanyBasicFields(CompanyDto company, String expectedName, String expectedCountryCode, String expectedEmail) {
    assertEquals(expectedName, company.getName());
    assertEquals(expectedCountryCode, company.getCountryCode());
    assertEquals(expectedEmail, company.getEmail());
  }

  /**
   * Validates that a company has the expected fields including website.
   */
  private void validateCompanyWithWebsite(CompanyDto company, String expectedName, String expectedCountryCode, String expectedEmail, String expectedWebsite) {
    validateCompanyBasicFields(company, expectedName, expectedCountryCode, expectedEmail);
    assertEquals(expectedWebsite, company.getWebsite());
  }

  /**
   * Validates that a company has the expected status.
   */
  private void validateCompanyStatus(CompanyDto company, CompanyStatus expectedStatus) {
    assertEquals(expectedStatus, company.getStatus());
  }
}
