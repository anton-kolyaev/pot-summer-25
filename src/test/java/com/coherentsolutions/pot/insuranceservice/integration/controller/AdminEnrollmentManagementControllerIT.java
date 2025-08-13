package com.coherentsolutions.pot.insuranceservice.integration.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.coherentsolutions.pot.insuranceservice.dto.enrollment.EnrollmentDto;
import com.coherentsolutions.pot.insuranceservice.enums.UserStatus;
import com.coherentsolutions.pot.insuranceservice.integration.IntegrationTestConfiguration;
import com.coherentsolutions.pot.insuranceservice.integration.containers.PostgresTestContainer;
import com.coherentsolutions.pot.insuranceservice.model.Company;
import com.coherentsolutions.pot.insuranceservice.model.Plan;
import com.coherentsolutions.pot.insuranceservice.model.PlanType;
import com.coherentsolutions.pot.insuranceservice.model.User;
import com.coherentsolutions.pot.insuranceservice.repository.CompanyRepository;
import com.coherentsolutions.pot.insuranceservice.repository.PlanRepository;
import com.coherentsolutions.pot.insuranceservice.repository.PlanTypeRepository;
import com.coherentsolutions.pot.insuranceservice.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
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
@DisplayName("Integration Test for AdminEnrollmentManagementController")
public class AdminEnrollmentManagementControllerIT extends PostgresTestContainer {

  private static final String ENDPOINT = "/v1/enrollments";

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PlanTypeRepository planTypeRepository;
  @Autowired
  private PlanRepository planRepository;

  @Autowired
  private CompanyRepository companyRepository;

  private UUID userId;
  private UUID planId;
  private EnrollmentDto baseRequest;

  private static EnrollmentDto copy(EnrollmentDto src) {
    EnrollmentDto d = new EnrollmentDto();
    d.setId(src.getId());
    d.setUserId(src.getUserId());
    d.setPlanId(src.getPlanId());
    d.setElectionAmount(src.getElectionAmount());
    d.setPlanContribution(src.getPlanContribution());
    return d;
  }

  @BeforeEach
  void setUp() {
    userId = seedUser();
    planId = seedPlan();

    baseRequest = new EnrollmentDto();
    baseRequest.setUserId(userId);
    baseRequest.setPlanId(planId);
    baseRequest.setElectionAmount(new BigDecimal("100.00")); // must be > 0
  }

  @Test
  @DisplayName("Should create enrollment successfully")
  void shouldCreateEnrollmentSuccessfully() throws Exception {
    EnrollmentDto createRequest = copy(baseRequest);

    String response = mockMvc.perform(post(ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(createRequest)))
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();

    EnrollmentDto created = objectMapper.readValue(response, EnrollmentDto.class);

    assertNotNull(created.getId());
    assertEquals(userId, created.getUserId());
    assertEquals(planId, created.getPlanId());
  }

  @Test
  @DisplayName("Should return 400 when active enrollment already exists for same user & plan")
  void shouldReturnBadRequestWhenDuplicateEnrollment() throws Exception {
    mockMvc.perform(post(ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(baseRequest)))
        .andExpect(status().isCreated());

    mockMvc.perform(post(ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(baseRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 404 when user not found")
  void shouldReturnNotFoundWhenUserMissing() throws Exception {
    EnrollmentDto req = copy(baseRequest);
    req.setUserId(UUID.randomUUID()); // non-existent
    mockMvc.perform(post(ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(req)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should return 404 when plan not found")
  void shouldReturnNotFoundWhenPlanMissing() throws Exception {
    EnrollmentDto req = copy(baseRequest);
    req.setPlanId(UUID.randomUUID()); // non-existent
    mockMvc.perform(post(ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(req)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should return 400 when electionAmount is negative or zero")
  void shouldReturnBadRequestWhenElectionInvalid() throws Exception {
    EnrollmentDto req = copy(baseRequest);
    req.setElectionAmount(new BigDecimal("-1.00"));
    mockMvc.perform(post(ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(req)))
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
  @DisplayName("Should return 415 when Content-Type is missing")
  void shouldReturnUnsupportedMediaTypeWhenNoContentType() throws Exception {
    mockMvc.perform(post(ENDPOINT)
            .content(toJson(baseRequest)))
        .andExpect(status().isUnsupportedMediaType());
  }

  @Test
  @DisplayName("Should return empty list when no enrollments")
  void shouldReturnEmptyListWhenNoEnrollments() throws Exception {
    String json = mockMvc.perform(get(ENDPOINT))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    EnrollmentDto[] array = objectMapper.readValue(json, EnrollmentDto[].class);
    List<EnrollmentDto> list = Arrays.asList(array);

    assertEquals(0, list.size());
  }

  @Test
  @DisplayName("Should list all active enrollments")
  void shouldListAllActiveEnrollments() throws Exception {

    String createdJson1 = mockMvc.perform(post(ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(baseRequest)))
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();

    UUID anotherPlanId = seedPlan();
    EnrollmentDto req2 = new EnrollmentDto();
    req2.setUserId(userId);
    req2.setPlanId(anotherPlanId);
    req2.setElectionAmount(new BigDecimal("50.00"));

    String createdJson2 = mockMvc.perform(post(ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(req2)))
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();

    String json = mockMvc.perform(get(ENDPOINT))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    EnrollmentDto[] array = objectMapper.readValue(json, EnrollmentDto[].class);
    List<EnrollmentDto> list = Arrays.asList(array);

    assertNotNull(list);
    assertEquals(2, list.size());
  }

  private UUID seedUser() {
    Company c = new Company();
    c.setName("Acme Inc.");
    c.setCountryCode("US");

    companyRepository.saveAndFlush(c);

    User u = new User();
    u.setFirstName("John");
    u.setLastName("Doe");
    u.setUsername("john.doe");
    u.setEmail("john.doe@example.com");
    u.setSsn("123-45-6789");
    u.setDateOfBirth(LocalDate.of(1990, 1, 1));
    u.setStatus(UserStatus.ACTIVE);
    u.setCompany(c);
    return userRepository.saveAndFlush(u).getId();
  }

  private UUID seedPlan() {
    PlanType dental = planTypeRepository.findByCode("DENTAL")
        .orElseGet(() -> {
          PlanType type = new PlanType();
          type.setCode("DENTAL");
          type.setName("Dental Plan");
          return planTypeRepository.save(type);
        });

    Plan p = new Plan();
    p.setName("Basic Dental Plan");
    p.setType(dental);
    p.setContribution(new BigDecimal("100.00"));
    return planRepository.save(p).getId();
  }

  private String toJson(Object obj) throws Exception {
    return objectMapper.writeValueAsString(obj);
  }
}
