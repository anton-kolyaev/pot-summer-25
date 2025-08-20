package com.coherentsolutions.pot.insuranceservice.integration.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
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
@DisplayName("Integration Test for AdminClaimManagementController")
public class AdminClaimManagementControllerIt extends PostgresTestContainer {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private CompanyRepository companyRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PlanTypeRepository planTypeRepository;
  @Autowired
  private PlanRepository planRepository;

  private Company company;
  private User user;
  private Plan plan;
  private EnrollmentDto enrollment;

  private String companyEndpoint(UUID companyId) {
    return "/v1/companies/" + companyId + "/claims";
  }

  @BeforeEach
  void setUp() throws Exception {
    company = createTestCompany("Acme", "USA", "acme@example.com", "https://acme.test");
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
  @DisplayName("GET /v1/companies/{companyId}/claims — returns all when no filters")
  void shouldReturnAllWhenNoFilters() throws Exception {
    createClaimViaApi(user.getId(), enrollment.getId(), new BigDecimal("25.00"));
    createClaimViaApi(user.getId(), enrollment.getId(), new BigDecimal("30.00"));

    String response = mockMvc.perform(
            get(companyEndpoint(company.getId()))
                .with(asCompanyClaimManager(company.getId()))
                .param("page", "0").param("size", "10"))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    var tree = objectMapper.readTree(response);
    List<ClaimDto> content = objectMapper.readValue(tree.get("content").toString(),
        new TypeReference<>() {
        });
    assertEquals(2, content.size());
  }

  @Test
  @DisplayName("GET /v1/companies/{companyId}/claims — filter by status, serviceDate range, userId")
  void shouldFilterByStatusDateAndUser() throws Exception {
    createClaimViaApi(user.getId(), enrollment.getId(), new BigDecimal("10.00"),
        LocalDate.now().minusDays(10));
    ClaimDto c2 = createClaimViaApi(user.getId(), enrollment.getId(), new BigDecimal("20.00"),
        LocalDate.now().minusDays(2));

    String response = mockMvc.perform(
            get(companyEndpoint(company.getId()))
                .with(asCompanyClaimManager(company.getId()))
                .param("serviceDateFrom", LocalDate.now().minusDays(5).toString())
                .param("serviceDateTo", LocalDate.now().toString())
                .param("userId", user.getId().toString())
                .param("page", "0").param("size", "10"))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    var tree = objectMapper.readTree(response);
    List<ClaimDto> content = objectMapper.readValue(tree.get("content").toString(),
        new TypeReference<>() {
        });
    assertEquals(1, content.size());
    assertEquals(c2.getId(), content.getFirst().getId());
  }

  @Test
  @DisplayName("GET /v1/companies/{companyId}/claims — filter by planName")
  void shouldFilterByPlanName() throws Exception {
    createClaimViaApi(user.getId(), enrollment.getId(), new BigDecimal("10.00"));

    PlanType vision = ensurePlanType("VISION", "Vision");
    Plan otherPlan = planRepository.save(
        buildPlan("Vision Premium", vision, new BigDecimal("299.99")));

    var enrollReq = EnrollmentDto.builder()
        .userId(user.getId())
        .planId(otherPlan.getId())
        .electionAmount(new BigDecimal("200.00"))
        .build();

    String resp = mockMvc.perform(
            post("/v1/enrollments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(enrollReq)))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();
    EnrollmentDto otherEnrollment = objectMapper.readValue(resp, EnrollmentDto.class);

    createClaimViaApi(user.getId(), otherEnrollment.getId(), new BigDecimal("15.00"));

    mockMvc.perform(get(companyEndpoint(company.getId()))
            .with(asCompanyClaimManager(company.getId()))
            .param("planName", "dental")
            .param("page", "0").param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.content[0].planName").value(plan.getName()));
  }

  @Test
  @DisplayName("GET /v1/companies/{companyId}/claims — filter by amountMin/amountMax (inclusive)")
  void shouldFilterByAmountRangeInclusive() throws Exception {
    createClaimViaApi(user.getId(), enrollment.getId(), new BigDecimal("10.00"));
    ClaimDto mid = createClaimViaApi(user.getId(), enrollment.getId(), new BigDecimal("20.00"));
    createClaimViaApi(user.getId(), enrollment.getId(), new BigDecimal("30.00"));

    mockMvc.perform(get(companyEndpoint(company.getId()))
            .with(asCompanyClaimManager(company.getId()))
            .param("amountMin", "20.00")
            .param("amountMax", "20.00")
            .param("page", "0").param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.content[0].id").value(mid.getId().toString()));
  }

  @Test
  @DisplayName("POST /v1/companies/{companyId}/claims/{id}/approval — approves PENDING claim")
  void shouldApprovePendingClaim() throws Exception {
    ClaimDto created = createClaimViaApi(user.getId(), enrollment.getId(), new BigDecimal("50.00"));

    var body = objectMapper.createObjectNode()
        .put("approvedAmount", "40.50")
        .put("notes", "ok by reviewer");

    var result = mockMvc.perform(
            post(companyEndpoint(company.getId()) + "/" + created.getId() + "/approval")
                .with(asCompanyClaimManager(company.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("APPROVED"))
        .andExpect(jsonPath("$.approvedAmount").value(40.50))
        .andReturn();

    ClaimDto approved = objectMapper.readValue(result.getResponse().getContentAsString(),
        ClaimDto.class);
    assertEquals(created.getId(), approved.getId());
    assertEquals(ClaimStatus.APPROVED, approved.getStatus());
  }

  @Test
  @DisplayName("POST /v1/companies/{companyId}/claims/{id}/denial — denies PENDING claim")
  void shouldDenyPendingClaim() throws Exception {
    ClaimDto created = createClaimViaApi(user.getId(), enrollment.getId(), new BigDecimal("60.00"));

    var body = objectMapper.createObjectNode()
        .put("reason", "Not covered service")
        .put("notes", "explained to member");

    mockMvc.perform(
            post(companyEndpoint(company.getId()) + "/" + created.getId() + "/denial")
                .with(asCompanyClaimManager(company.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("DENIED"))
        .andExpect(jsonPath("$.deniedReason").value("Not covered service"));
  }

  @Test
  @DisplayName("GET /v1/companies/{companyId}/claims/{id} — returns claim by id")
  void shouldGetClaimById() throws Exception {
    ClaimDto created = createClaimViaApi(user.getId(), enrollment.getId(), new BigDecimal("42.00"));

    mockMvc.perform(get(companyEndpoint(company.getId()) + "/" + created.getId())
            .with(asCompanyClaimManager(company.getId())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(created.getId().toString()))
        .andExpect(jsonPath("$.claimNumber").value(created.getClaimNumber()))
        .andExpect(jsonPath("$.status").value("PENDING"))
        .andExpect(jsonPath("$.planName").value(plan.getName()))
        .andExpect(jsonPath("$.consumer.userId").value(user.getId().toString()));
  }

  @Test
  @DisplayName("GET /v1/companies/{companyId}/claims — returns 403 when called by CONSUMER")
  void shouldForbidConsumerAccessingCompanyClaims() throws Exception {
    createClaimViaApi(user.getId(), enrollment.getId(), new BigDecimal("25.00"));

    mockMvc.perform(get(companyEndpoint(company.getId()))
            .with(asConsumer(user.getId()))
            .param("page", "0").param("size", "10"))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("GET /v1/companies/{companyId}/claims — returns 403 when claim manager from another company")
  void shouldForbidClaimManagerFromOtherCompany() throws Exception {
    createClaimViaApi(user.getId(), enrollment.getId(), new BigDecimal("25.00"));

    Company other = createTestCompany("OtherCo", "LT", "other@example.com", "https://other.test");

    mockMvc.perform(get(companyEndpoint(company.getId()))
            .with(asCompanyClaimManager(other.getId()))
            .param("page", "0").param("size", "10"))
        .andExpect(status().isForbidden());
  }


  private ClaimDto createClaimViaApi(UUID userId, UUID enrollmentId, BigDecimal amount)
      throws Exception {
    return createClaimViaApi(userId, enrollmentId, amount, LocalDate.now());
  }

  private ClaimDto createClaimViaApi(UUID userId, UUID enrollmentId, BigDecimal amount,
      LocalDate date) throws Exception {
    ClaimDto req = ClaimDto.builder()
        .serviceDate(date)
        .amount(amount)
        .enrollmentId(enrollmentId)
        .consumer(ConsumerDto.builder().userId(userId).build())
        .build();

    String response = mockMvc.perform(
            post("/v1/users/" + userId + "/claims")
                .with(asConsumer(userId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();

    return objectMapper.readValue(response, ClaimDto.class);
  }

  private RequestPostProcessor asCompanyClaimManager(UUID companyId) {
    return jwt().jwt(j -> j.claim("company_id", companyId.toString()))
        .authorities(new SimpleGrantedAuthority("ROLE_FUNC_COMPANY_CLAIM_MANAGER"));
  }

  private RequestPostProcessor asConsumer(UUID userId) {
    return jwt().jwt(j -> j.claim("user_uuid", userId.toString()))
        .authorities(new SimpleGrantedAuthority("ROLE_CONSUMER"));
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
