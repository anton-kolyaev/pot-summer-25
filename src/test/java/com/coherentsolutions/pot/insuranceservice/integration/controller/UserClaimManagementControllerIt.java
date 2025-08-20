package com.coherentsolutions.pot.insuranceservice.integration.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import com.coherentsolutions.pot.insuranceservice.dto.claim.ClaimDto;
import com.coherentsolutions.pot.insuranceservice.dto.consumer.ConsumerDto;
import com.coherentsolutions.pot.insuranceservice.dto.enrollment.EnrollmentDto;
import com.coherentsolutions.pot.insuranceservice.enums.ClaimStatus;
import com.coherentsolutions.pot.insuranceservice.enums.CompanyStatus;
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
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(IntegrationTestConfiguration.class)
@Transactional
@DisplayName("Integration Test for UserClaimManagementController")
public class UserClaimManagementControllerIt extends PostgresTestContainer {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Autowired private CompanyRepository companyRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private PlanTypeRepository planTypeRepository;
  @Autowired private PlanRepository planRepository;

  private User user;
  private Plan plan;
  private EnrollmentDto enrollment;

  private String userEndpoint(UUID userId) {
    return "/v1/users/" + userId + "/claims";
  }

  @BeforeEach
  void setUp() throws Exception {
    Company company = createTestCompany("Acme", "USA", "acme@example.com", "https://acme.test");
    user = createTestUser("Alice", "Doe", "alice", "alice@example.com", company, "123-45-6789");
    PlanType dental = ensurePlanType("DENTAL", "Dental");
    plan = planRepository.save(buildPlan("Dental Basic", dental, new BigDecimal("199.99")));

    var enrollReq = EnrollmentDto.builder()
        .userId(user.getId())
        .planId(plan.getId())
        .electionAmount(new BigDecimal("100.00"))
        .build();
    
    String resp = mockMvc.perform(
            post("/v1/enrollments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(enrollReq)))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();

    enrollment = objectMapper.readValue(resp, EnrollmentDto.class);
  }

  @Test
  @DisplayName("POST /v1/users/{userId}/claims — creates claim successfully with defaults")
  void shouldCreateClaimSuccessfully() throws Exception {
    ClaimDto req = buildClaimDto(user.getId(), enrollment.getId(), LocalDate.now(),
        new BigDecimal("50.00"));

    var result = mockMvc.perform(
            post(userEndpoint(user.getId()))
                .with(asConsumer(user.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value("PENDING"))
        .andReturn();

    ClaimDto created = objectMapper.readValue(result.getResponse().getContentAsString(),
        ClaimDto.class);

    assertNotNull(created.getId());
    assertEquals(created.getId().toString(), created.getClaimNumber(), "claimNumber should mirror id");
    assertEquals(ClaimStatus.PENDING, created.getStatus());
    assertEquals(user.getId(), created.getConsumer().getUserId());
  }

  @Test
  @DisplayName("POST /v1/users/{userId}/claims — returns 400 when enrollmentId is missing")
  void shouldReturnBadRequestWhenEnrollmentMissing() throws Exception {
    ClaimDto req = buildClaimDto(user.getId(), null, LocalDate.now(), new BigDecimal("10.00"));

    mockMvc.perform(
            post(userEndpoint(user.getId()))
                .with(asConsumer(user.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("POST /v1/users/{userId}/claims — returns 404 when enrollment not found")
  void shouldReturnNotFoundWhenEnrollmentMissing() throws Exception {
    ClaimDto req = buildClaimDto(user.getId(), UUID.randomUUID(), LocalDate.now(),
        new BigDecimal("10.00"));

    mockMvc.perform(
            post(userEndpoint(user.getId()))
                .with(asConsumer(user.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("POST /v1/users/{userId}/claims — ignores client-provided status; always PENDING")
  void shouldIgnoreClientProvidedStatusOnCreate() throws Exception {
    ClaimDto req = buildClaimDto(user.getId(), enrollment.getId(), LocalDate.now(),
        new BigDecimal("12.34"));
    req.setStatus(ClaimStatus.APPROVED);

    String response = mockMvc.perform(
            post(userEndpoint(user.getId()))
                .with(asConsumer(user.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value("PENDING"))
        .andReturn().getResponse().getContentAsString();

    ClaimDto created = objectMapper.readValue(response, ClaimDto.class);
    assertEquals(ClaimStatus.PENDING, created.getStatus());
  }
  @Test
  @DisplayName("POST /v1/users/{userId}/claims — returns 403 when caller is not a CONSUMER")
  void shouldForbidWhenNotConsumer() throws Exception {
    ClaimDto req = buildClaimDto(user.getId(), enrollment.getId(), LocalDate.now(), new BigDecimal("50.00"));

    mockMvc.perform(
            post(userEndpoint(user.getId()))
                .with(asCompanyClaimManager())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("POST /v1/users/{userId}/claims — returns 403 when consumer tries to create claim for another user")
  void shouldForbidWhenConsumerTriesForOtherUser() throws Exception {
    UUID otherUserId = UUID.randomUUID();

    ClaimDto req = buildClaimDto(otherUserId, enrollment.getId(), LocalDate.now(), new BigDecimal("50.00"));

    mockMvc.perform(
            post(userEndpoint(otherUserId))
                .with(asConsumer(user.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isForbidden());
  }

  

  private RequestPostProcessor asConsumer(UUID userId) {
    return jwt().jwt(j -> j.claim("user_uuid", userId.toString()))
        .authorities(new SimpleGrantedAuthority("ROLE_CONSUMER"));
  }
  private RequestPostProcessor asCompanyClaimManager() {
    return jwt().authorities(new SimpleGrantedAuthority("ROLE_FUNC_COMPANY_CLAIM_MANAGER"));
  }
  private ClaimDto buildClaimDto(UUID userId, UUID enrollmentId, LocalDate serviceDate,
      BigDecimal amount) {
    return ClaimDto.builder()
        .serviceDate(serviceDate)
        .amount(amount)
        .enrollmentId(enrollmentId)
        .consumer(ConsumerDto.builder().userId(userId).build())
        .build();
  }

  private Company createTestCompany(String name, String countryCode, String email, String website) {
    Company company = new Company();
    company.setName(name);
    company.setCountryCode(countryCode);
    company.setEmail(email);
    company.setWebsite(website);
    company.setStatus(CompanyStatus.ACTIVE);
    return companyRepository.save(company);
  }

  private User createTestUser(String firstName, String lastName, String username, String email,
      Company c, String ssn) {
    var user = new User();
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setUsername(username);
    user.setEmail(email);
    user.setCompany(c);
    user.setStatus(UserStatus.ACTIVE);
    user.setDateOfBirth(LocalDate.of(1990, 1, 1));
    user.setSsn(ssn);
    return userRepository.save(user);
  }

  private PlanType ensurePlanType(String code, String name) {
    return planTypeRepository.findByCode(code)
        .orElseGet(() -> planTypeRepository.save(buildPlanType(code, name)));
  }

  private PlanType buildPlanType(String code, String name) {
    var planType = new PlanType();
    planType.setCode(code);
    planType.setName(name);
    return planType;
  }

  private Plan buildPlan(String name, PlanType type, BigDecimal contribution) {
    var plan = new Plan();
    plan.setName(name);
    plan.setType(type);
    plan.setContribution(contribution);
    return plan;
  }
}
