package com.coherentsolutions.pot.insuranceservice.integration.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.coherentsolutions.pot.insuranceservice.dto.claim.ClaimDto;
import com.coherentsolutions.pot.insuranceservice.dto.consumer.ConsumerDto;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(IntegrationTestConfiguration.class)
@Transactional
@DisplayName("Integration Test for ClaimManagementController")
public class ClaimManagementControllerIt extends PostgresTestContainer {

  private static final String ENDPOINT = "/v1/claims";

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

  private User user;
  private Plan plan;

  @BeforeEach
  void setUp() {
    // Given
    Company company = createTestCompany("Acme", "USA", "acme@example.com", "https://acme.test");
    user = createTestUser("Alice", "Doe", "alice", "alice@example.com", company, "123-45-6789");
    PlanType dental = ensurePlanType("DENTAL", "Dental");
    plan = planRepository.save(buildPlan("Dental Basic", dental, new BigDecimal("199.99")));
  }

  @Test
  @DisplayName("POST /v1/claims — should create claim successfully (status defaults to PENDING, claimNumber mirrors id)")
  void shouldCreateClaimSuccessfully() throws Exception {
    // Given
    ClaimDto req = buildClaimDto(user.getId(), plan.getId(), LocalDate.now(),
        new BigDecimal("50.00"));
    req.setClaimNumber("client-should-not-set");

    // When
    var result = mockMvc.perform(
            post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createClaimJson(
                    req.getConsumer().getUserId(),
                    req.getPlanId(),
                    req.getServiceDate(),
                    req.getAmount(),
                    req.getStatus(),
                    req.getClaimNumber()
                )))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.planName").value(plan.getName()));

    String response = result.andReturn().getResponse().getContentAsString();
    ClaimDto created = objectMapper.readValue(response, ClaimDto.class);

    // Then
    assertNotNull(created.getId());
    assertNotNull(created.getClaimNumber());
    assertEquals(created.getId().toString(), created.getClaimNumber());
    assertEquals(ClaimStatus.PENDING, created.getStatus());
    assertEquals(req.getServiceDate(), created.getServiceDate());
    assertEquals(req.getAmount(), created.getAmount());
    assertEquals(user.getId(), created.getConsumer().getUserId());
  }


  @Test
  @DisplayName("POST /v1/claims — should return 400 when consumer.userId is missing")
  void shouldReturnBadRequestWhenConsumerMissing() throws Exception {
    // When / Then
    mockMvc.perform(
            post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createClaimJson(
                    null,
                    plan.getId(),
                    LocalDate.now(),
                    new BigDecimal("10.00")
                )))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("POST /v1/claims — should return 404 when plan not found")
  void shouldReturnNotFoundWhenPlanMissing() throws Exception {
    // When / Then
    mockMvc.perform(
            post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createClaimJson(
                    user.getId(),
                    UUID.randomUUID(),
                    LocalDate.now(),
                    new BigDecimal("10.00")
                )))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("GET /v1/claims — returns all when no filters")
  void shouldReturnAllWhenNoFilters() throws Exception {
    // Given
    createClaimViaApi(buildClaimDto(user.getId(), plan.getId(), LocalDate.now().minusDays(1),
        new BigDecimal("25.00")));
    createClaimViaApi(
        buildClaimDto(user.getId(), plan.getId(), LocalDate.now(), new BigDecimal("30.00")));

    // When
    String response = mockMvc.perform(
            get(ENDPOINT).param("page", "0").param("size", "10")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    // Then
    var tree = objectMapper.readTree(response);
    List<ClaimDto> content = objectMapper.readValue(tree.get("content").toString(),
        new TypeReference<List<ClaimDto>>() {
        });
    assertEquals(2, content.size());
  }

  @Test
  @DisplayName("GET /v1/claims — filter by status, serviceDate range, userId")
  void shouldFilterByStatusDateAndUser() throws Exception {
    // Given
    createClaimViaApi(buildClaimDto(user.getId(), plan.getId(), LocalDate.now().minusDays(10),
        new BigDecimal("10.00")));
    ClaimDto c2 = createClaimViaApi(
        buildClaimDto(user.getId(), plan.getId(), LocalDate.now().minusDays(2),
            new BigDecimal("20.00")));

    // When
    String response = mockMvc.perform(
            get(ENDPOINT)
                .param("serviceDateFrom", LocalDate.now().minusDays(5).toString())
                .param("serviceDateTo", LocalDate.now().toString())
                .param("userId", user.getId().toString())
                .param("page", "0").param("size", "10")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    // Then
    var tree = objectMapper.readTree(response);
    List<ClaimDto> content = objectMapper.readValue(tree.get("content").toString(),
        new TypeReference<>() {
        });
    assertEquals(1, content.size());
    assertEquals(c2.getId(), content.getFirst().getId());
  }

  @Test
  @DisplayName("POST /v1/claims — ignores client-provided status; always PENDING on create")
  void shouldIgnoreClientProvidedStatusOnCreate() throws Exception {
    // Given
    ClaimDto req = buildClaimDto(user.getId(), plan.getId(), LocalDate.now(),
        new BigDecimal("12.34"));

    // When
    String response = mockMvc.perform(
            post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createClaimJson(
                    req.getConsumer().getUserId(),
                    req.getPlanId(),
                    req.getServiceDate(),
                    req.getAmount(),
                    ClaimStatus.APPROVED, // client tries to set it
                    null
                )))
        // Then
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value("PENDING"))
        .andReturn()
        .getResponse()
        .getContentAsString();

    // Then
    ClaimDto created = objectMapper.readValue(response, ClaimDto.class);
    assertEquals(ClaimStatus.PENDING, created.getStatus());
  }

  @Test
  @DisplayName("GET /v1/claims — filter by planName (case-insensitive contains)")
  void shouldFilterByPlanName() throws Exception {
    // Given
    createClaimViaApi(
        buildClaimDto(user.getId(), plan.getId(), LocalDate.now(), new BigDecimal("10.00")));
    PlanType dental = ensurePlanType("DENTAL", "Dental");
    Plan otherPlan = planRepository.save(
        buildPlan("Vision Premium", dental, new BigDecimal("299.99")));
    createClaimViaApi(
        buildClaimDto(user.getId(), otherPlan.getId(), LocalDate.now(), new BigDecimal("15.00")));

    // When / Then
    mockMvc.perform(get(ENDPOINT)
            .param("planName", "dental")
            .param("page", "0").param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.content[0].planName").value(plan.getName()));
  }

  @Test
  @DisplayName("GET /v1/claims — filter by amountMin/amountMax (inclusive)")
  void shouldFilterByAmountRangeInclusive() throws Exception {
    // Given
    createClaimViaApi(
        buildClaimDto(user.getId(), plan.getId(), LocalDate.now(), new BigDecimal("10.00")));
    ClaimDto mid = createClaimViaApi(
        buildClaimDto(user.getId(), plan.getId(), LocalDate.now(), new BigDecimal("20.00")));
    createClaimViaApi(
        buildClaimDto(user.getId(), plan.getId(), LocalDate.now(), new BigDecimal("30.00")));

    // When / Then
    mockMvc.perform(get(ENDPOINT)
            .param("amountMin", "20.00")
            .param("amountMax", "20.00")
            .param("page", "0").param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.content[0].id").value(mid.getId().toString()));
  }

  private ClaimDto buildClaimDto(UUID userId, UUID planId, LocalDate serviceDate,
      BigDecimal amount) {
    return ClaimDto.builder()
        .serviceDate(serviceDate)
        .amount(amount)
        .planId(planId)
        .consumer(ConsumerDto.builder().userId(userId).build())
        .build();
  }

  private ClaimDto createClaimViaApi(ClaimDto req) throws Exception {
    String response = mockMvc.perform(
            post(ENDPOINT).contentType(MediaType.APPLICATION_JSON)
                .content(createClaimJson(
                    req.getConsumer().getUserId(),
                    req.getPlanId(),
                    req.getServiceDate(),
                    req.getAmount(),
                    req.getStatus(),
                    req.getClaimNumber()
                )))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();
    return objectMapper.readValue(response, ClaimDto.class);
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
    User u = new User();
    u.setFirstName(firstName);
    u.setLastName(lastName);
    u.setUsername(username);
    u.setEmail(email);
    u.setCompany(c);
    u.setStatus(UserStatus.ACTIVE);
    u.setDateOfBirth(LocalDate.of(1990, 1, 1));
    u.setSsn(ssn);
    return userRepository.save(u);
  }

  private PlanType buildPlanType(String code, String name) {
    PlanType t = new PlanType();
    t.setCode(code);
    t.setName(name);
    return t;
  }

  private Plan buildPlan(String name, PlanType type, BigDecimal contribution) {
    Plan p = new Plan();
    p.setName(name);
    p.setType(type);
    p.setContribution(contribution);
    return p;
  }

  private PlanType ensurePlanType(String code, String name) {
    return planTypeRepository.findByCode(code)
        .orElseGet(() -> planTypeRepository.save(buildPlanType(code, name)));
  }

  private String createClaimJson(
      UUID userId, UUID planId, LocalDate serviceDate, BigDecimal amount,
      ClaimStatus status, String claimNumber
  ) throws Exception {
    var root = objectMapper.createObjectNode();
    root.put("serviceDate", serviceDate.toString());
    root.put("amount", amount);
    root.put("planId", planId.toString());
    if (status != null) {
      root.put("status", status.name());
    }
    if (claimNumber != null) {
      root.put("claimNumber", claimNumber);
    }
    if (userId != null) {
      var consumer = root.putObject("consumer");
      consumer.put("userId", userId.toString());
    }
    return objectMapper.writeValueAsString(root);
  }

  private String createClaimJson(UUID userId, UUID planId, LocalDate serviceDate, BigDecimal amount)
      throws Exception {
    return createClaimJson(userId, planId, serviceDate, amount, null, null);
  }
}
