package com.coherentsolutions.pot.insuranceservice.integration.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.coherentsolutions.pot.insuranceservice.dto.plan.PlanDto;
import com.coherentsolutions.pot.insuranceservice.integration.IntegrationTestConfiguration;
import com.coherentsolutions.pot.insuranceservice.integration.TestSecurityUtils;
import com.coherentsolutions.pot.insuranceservice.integration.containers.PostgresTestContainer;
import com.coherentsolutions.pot.insuranceservice.model.PlanType;
import com.coherentsolutions.pot.insuranceservice.repository.PlanRepository;
import com.coherentsolutions.pot.insuranceservice.repository.PlanTypeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
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
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(IntegrationTestConfiguration.class)
@Transactional
@DisplayName("Integration Test for AdminPlanManagementController")
public class AdminPlanManagementControllerIt extends PostgresTestContainer {

  private static final String ENDPOINT = "/v1/companies/2655936b-bfa1-4e6b-981a-223f82b92231/plans";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private PlanTypeRepository planTypeRepository;

  @Autowired
  private PlanRepository planRepository;

  private Integer dentalTypeId;

  @BeforeEach
  void setUp() {
    dentalTypeId = planTypeRepository.findByCode("DENTAL")
        .map(PlanType::getId)
        .orElseGet(() -> {
          PlanType dental = new PlanType();
          dental.setCode("DENTAL");
          dental.setName("Dental Plan");
          // Manually set audit fields for test
          dental.setCreatedAt(Instant.now());
          dental.setCreatedBy(UUID.fromString("00000000-0000-0000-0000-000000000000"));
          return planTypeRepository.save(dental).getId();
        });
  }

  private PlanDto buildPlanDto(String name, Integer typeId, BigDecimal contribution) {
    return PlanDto.builder()
        .name(name)
        .type(typeId)
        .contribution(contribution)
        .build();
  }

  private String toJson(Object obj) throws Exception {
    return objectMapper.writeValueAsString(obj);
  }

  private PlanType buildPlanType(String code, String name) {
    PlanType type = new PlanType();
    type.setCode(code);
    type.setName(name);
    // Manually set audit fields for test
    type.setCreatedAt(Instant.now());
    type.setCreatedBy(UUID.fromString("00000000-0000-0000-0000-000000000000"));
    return type;
  }

  @Test
  @DisplayName("Should create plan successfully")
  void shouldCreatePlanSuccessfully() throws Exception {

    PlanDto createRequest = buildPlanDto("Basic Dental Plan", dentalTypeId,
        new BigDecimal("199.99"));

    String response = mockMvc.perform(post(ENDPOINT)
            .with(TestSecurityUtils.adminUser())
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(createRequest)))
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();

    PlanDto created = objectMapper.readValue(response, PlanDto.class);

    assertNotNull(created.getId());
    assertEquals("Basic Dental Plan", created.getName());
    assertEquals(dentalTypeId, created.getType());
    assertEquals(new BigDecimal("199.99"), created.getContribution());
  }

  @Test
  @DisplayName("Should return 400 for invalid plan type")
  void shouldReturnBadRequestForInvalidPlanType() throws Exception {
    PlanDto createRequest = buildPlanDto("Invalid Type Plan", 9999, new BigDecimal("123.45"));

    mockMvc.perform(post(ENDPOINT)
            .with(TestSecurityUtils.adminUser())
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(createRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 400 for negative contribution")
  void shouldReturnBadRequestForNegativeContribution() throws Exception {
    PlanDto createRequest = buildPlanDto("Negative Contribution Plan", dentalTypeId,
        new BigDecimal("-10.00"));

    mockMvc.perform(post(ENDPOINT)
            .with(TestSecurityUtils.adminUser())
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(createRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 400 for missing name")
  void shouldReturnBadRequestForMissingName() throws Exception {
    PlanDto createRequest = PlanDto.builder()
        .type(dentalTypeId)
        .contribution(new BigDecimal("150.00"))
        .build();

    mockMvc.perform(post(ENDPOINT)
            .with(TestSecurityUtils.adminUser())
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(createRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 400 for null body")
  void shouldReturnBadRequestForNullBody() throws Exception {
    mockMvc.perform(post(ENDPOINT)
            .with(TestSecurityUtils.adminUser())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 400 for invalid JSON")
  void shouldReturnBadRequestForInvalidJson() throws Exception {
    String invalidJson = "{ name: }";

    mockMvc.perform(post(ENDPOINT)
            .with(TestSecurityUtils.adminUser())
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 415 for missing content type")
  void shouldReturnUnsupportedMediaTypeForMissingContentType() throws Exception {
    PlanDto createRequest = buildPlanDto("No Content Type", dentalTypeId, new BigDecimal("99.99"));

    mockMvc.perform(post(ENDPOINT)
            .with(TestSecurityUtils.adminUser())
            .content(toJson(createRequest)))
        .andExpect(status().isUnsupportedMediaType());
  }

  @Test
  @DisplayName("Should update plan successfully")
  void shouldUpdatePlanSuccessfully() throws Exception {

    PlanDto createRequest = buildPlanDto("Original Plan", dentalTypeId, new BigDecimal("150.00"));

    String createResponse = mockMvc.perform(post(ENDPOINT)
            .with(TestSecurityUtils.adminUser())
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(createRequest)))
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();

    PlanDto created = objectMapper.readValue(createResponse, PlanDto.class);

    PlanDto updateRequest = buildPlanDto("Updated Plan", dentalTypeId, new BigDecimal("299.99"));

    String updateResponse = mockMvc.perform(put(ENDPOINT + "/" + created.getId())
            .with(TestSecurityUtils.adminUser())
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(updateRequest)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    PlanDto updated = objectMapper.readValue(updateResponse, PlanDto.class);

    assertNotNull(updated.getId());
    assertEquals("Updated Plan", updated.getName());
    assertEquals(dentalTypeId, updated.getType());
    assertEquals(new BigDecimal("299.99"), updated.getContribution());
  }

  @Test
  @DisplayName("Should return 404 when updating non-existent plan")
  void shouldReturnNotFoundForNonExistentPlan() throws Exception {
    UUID nonExistentId = UUID.randomUUID();

    PlanDto updateRequest = buildPlanDto("Ghost Plan", dentalTypeId, new BigDecimal("123.45"));

    mockMvc.perform(put(ENDPOINT + "/" + nonExistentId)
            .with(TestSecurityUtils.adminUser())
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(updateRequest)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should return 400 when trying to change plan type on update")
  void shouldReturnBadRequestWhenChangingPlanTypeOnUpdate() throws Exception {

    PlanDto createRequest = buildPlanDto("Temporary Plan", dentalTypeId, new BigDecimal("150.00"));

    String createResponse = mockMvc.perform(post(ENDPOINT)
            .with(TestSecurityUtils.adminUser())
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(createRequest)))
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();

    PlanDto created = objectMapper.readValue(createResponse, PlanDto.class);

    created.setType(9999);

    mockMvc.perform(put(ENDPOINT + "/" + created.getId())
            .with(TestSecurityUtils.adminUser())
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(created)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 400 for missing fields on update")
  void shouldReturnBadRequestForMissingFieldsOnUpdate() throws Exception {
    UUID id = UUID.randomUUID();

    PlanDto invalidDto = new PlanDto();

    mockMvc.perform(put(ENDPOINT + "/" + id)
            .with(TestSecurityUtils.adminUser())
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(invalidDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return plans filtered by type")
  void shouldReturnPlansFilteredByType() throws Exception {

    PlanDto plan1 = buildPlanDto("Dental Plan A", dentalTypeId, new BigDecimal("100.00"));
    PlanDto plan2 = buildPlanDto("Dental Plan B", dentalTypeId, new BigDecimal("200.00"));

    mockMvc.perform(post(ENDPOINT)
            .with(TestSecurityUtils.adminUser())
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(plan1)))
        .andExpect(status().isCreated());

    mockMvc.perform(post(ENDPOINT)
            .with(TestSecurityUtils.adminUser())
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(plan2)))
        .andExpect(status().isCreated());

    // When / Then
    mockMvc.perform(get(ENDPOINT)
            .with(TestSecurityUtils.adminUser())
            .param("typeId", String.valueOf(dentalTypeId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].name").value("Dental Plan A"))
        .andExpect(jsonPath("$[1].name").value("Dental Plan B"));
  }

  @Test
  @DisplayName("Should return all plans when no filter is provided")
  void shouldReturnAllPlansWithoutFilter() throws Exception {

    PlanDto plan1 = buildPlanDto("Generic Plan A", dentalTypeId, new BigDecimal("150.00"));
    PlanDto plan2 = buildPlanDto("Generic Plan B", dentalTypeId, new BigDecimal("250.00"));

    mockMvc.perform(post(ENDPOINT)
            .with(TestSecurityUtils.adminUser())
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(plan1)))
        .andExpect(status().isCreated());

    mockMvc.perform(post(ENDPOINT)
            .with(TestSecurityUtils.adminUser())
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(plan2)))
        .andExpect(status().isCreated());

    mockMvc.perform(get(ENDPOINT)
            .with(TestSecurityUtils.adminUser()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2));
  }

  @Test
  @DisplayName("Should return all plan types")
  void shouldReturnAllPlanTypes() throws Exception {
    // Create MEDICAL plan type for this test
    PlanType medical = buildPlanType("MEDICAL", "Medical Plan");
    planTypeRepository.save(medical);
    
    mockMvc.perform(get(ENDPOINT + "/plan-types")
            .with(TestSecurityUtils.adminUser()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[?(@.code=='DENTAL')]").exists())
        .andExpect(jsonPath("$[?(@.code=='MEDICAL')]").exists());
  }

  @Test
  @DisplayName("Should return empty list when no plan types exist")
  void shouldReturnEmptyListWhenNoPlanTypesExist() throws Exception {
    planTypeRepository.deleteAll();

    mockMvc.perform(get(ENDPOINT + "/plan-types")
            .with(TestSecurityUtils.adminUser()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  @DisplayName("Should soft delete plan successfully")
  void shouldSoftDeletePlanSuccessfully() throws Exception {

    PlanDto createRequest = buildPlanDto("Soft Delete Plan", dentalTypeId,
        new BigDecimal("199.99"));

    String createResponse = mockMvc.perform(post(ENDPOINT)
            .with(TestSecurityUtils.adminUser())
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(createRequest)))
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();

    PlanDto created = objectMapper.readValue(createResponse, PlanDto.class);

    mockMvc.perform(delete(ENDPOINT + "/" + created.getId())
            .with(TestSecurityUtils.adminUser()))
        .andExpect(status().isNoContent());

    mockMvc.perform(get(ENDPOINT)
            .with(TestSecurityUtils.adminUser()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[?(@.id=='" + created.getId() + "')]").doesNotExist());

    Instant deletedAt = planRepository.findDeletedAtById(created.getId());
    assertNotNull(deletedAt, "deletedAt should be set for soft-deleted plan");
  }

  @Test
  @DisplayName("Should return 404 when trying to delete non-existent or already deleted plan")
  void shouldReturnNotFoundForDeletingInvalidPlan() throws Exception {
    UUID nonExistentId = UUID.randomUUID();

    mockMvc.perform(delete(ENDPOINT + "/" + nonExistentId)
            .with(TestSecurityUtils.adminUser()))
        .andExpect(status().isNotFound());
  }
}
