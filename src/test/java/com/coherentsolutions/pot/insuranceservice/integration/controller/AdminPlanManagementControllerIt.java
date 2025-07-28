package com.coherentsolutions.pot.insuranceservice.integration.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.coherentsolutions.pot.insuranceservice.dto.plan.PlanDto;
import com.coherentsolutions.pot.insuranceservice.integration.IntegrationTestConfiguration;
import com.coherentsolutions.pot.insuranceservice.integration.containers.PostgresTestContainer;
import com.coherentsolutions.pot.insuranceservice.model.PlanType;
import com.coherentsolutions.pot.insuranceservice.repository.PlanTypeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(IntegrationTestConfiguration.class)
@Transactional
@DisplayName("Integration Test for AdminPlanManagementController")
public class AdminPlanManagementControllerIt extends PostgresTestContainer {

  private static final String ENDPOINT = "/v1/plans";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private PlanTypeRepository planTypeRepository;
  
  private Integer dentalTypeId;

  @BeforeEach
  void setUp() {
    dentalTypeId = planTypeRepository.findByCode("DENTAL")
        .map(PlanType::getId)
        .orElseGet(() -> {
          PlanType dental = new PlanType();
          dental.setCode("DENTAL");
          dental.setName("Dental Plan");
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

  @Test
  @DisplayName("Should create plan successfully")
  void shouldCreatePlanSuccessfully() throws Exception {
    PlanDto createRequest = buildPlanDto("Basic Dental Plan", dentalTypeId,
        new BigDecimal("199.99"));

    String response = mockMvc.perform(post(ENDPOINT)
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
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(createRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 400 for null body")
  void shouldReturnBadRequestForNullBody() throws Exception {
    mockMvc.perform(post(ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 400 for invalid JSON")
  void shouldReturnBadRequestForInvalidJson() throws Exception {
    String invalidJson = "{ name: }";

    mockMvc.perform(post(ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 415 for missing content type")
  void shouldReturnUnsupportedMediaTypeForMissingContentType() throws Exception {
    PlanDto createRequest = buildPlanDto("No Content Type", dentalTypeId, new BigDecimal("99.99"));

    mockMvc.perform(post(ENDPOINT)
            .content(toJson(createRequest)))
        .andExpect(status().isUnsupportedMediaType());
  }
}
