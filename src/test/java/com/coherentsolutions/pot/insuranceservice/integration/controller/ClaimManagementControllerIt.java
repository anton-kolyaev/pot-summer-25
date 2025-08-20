package com.coherentsolutions.pot.insuranceservice.integration.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(IntegrationTestConfiguration.class)
@Transactional
@DisplayName("Integration Test for ClaimManagementController (consumer + enrollment)")
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
  private EnrollmentDto enrollment;

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
  @DisplayName("POST /v1/claims — should create claim successfully (status defaults to PENDING, claimNumber mirrors id)")
  void shouldCreateClaimSuccessfully() throws Exception {
    // Given
    ClaimDto req = buildClaimDto(user.getId(), enrollment.getId(), LocalDate.now(),
        new BigDecimal("50.00"));
    req.setClaimNumber("client-should-not-set");

    // When
    var result = mockMvc.perform(
            post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createClaimJson(
                    req.getConsumer().getUserId(),
                    req.getEnrollmentId(),
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
    assertNotNull(created.getId(), "created id is null");
    assertNotNull(created.getClaimNumber(), "claimNumber is null");
    assertEquals(created.getId().toString(), created.getClaimNumber());
    assertEquals(ClaimStatus.PENDING, created.getStatus());
    assertEquals(req.getServiceDate(), created.getServiceDate());
    assertEquals(req.getAmount(), created.getAmount());
    assertEquals(user.getId(), created.getConsumer().getUserId());
  }

  @Test
  @DisplayName("POST /v1/claims — should return 400 when enrollmentId is missing")
  void shouldReturnBadRequestWhenEnrollmentMissing() throws Exception {
    mockMvc.perform(
            post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createClaimJson(
                    user.getId(),
                    null,
                    LocalDate.now(),
                    new BigDecimal("10.00")
                )))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("POST /v1/claims — should return 404 when enrollment not found")
  void shouldReturnNotFoundWhenEnrollmentMissing() throws Exception {
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
    createClaimViaApi(buildClaimDto(user.getId(), enrollment.getId(), LocalDate.now().minusDays(1),
        new BigDecimal("25.00")));
    createClaimViaApi(
        buildClaimDto(user.getId(), enrollment.getId(), LocalDate.now(), new BigDecimal("30.00")));

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
    createClaimViaApi(buildClaimDto(user.getId(), enrollment.getId(), LocalDate.now().minusDays(10),
        new BigDecimal("10.00")));
    ClaimDto c2 = createClaimViaApi(
        buildClaimDto(user.getId(), enrollment.getId(), LocalDate.now().minusDays(2),
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
    ClaimDto req = buildClaimDto(user.getId(), enrollment.getId(), LocalDate.now(),
        new BigDecimal("12.34"));

    // When / Then
    String response = mockMvc.perform(
            post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createClaimJson(
                    req.getConsumer().getUserId(),
                    req.getEnrollmentId(),
                    req.getServiceDate(),
                    req.getAmount(),
                    ClaimStatus.APPROVED,
                    null
                )))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value("PENDING"))
        .andReturn()
        .getResponse()
        .getContentAsString();

    ClaimDto created = objectMapper.readValue(response, ClaimDto.class);
    assertEquals(ClaimStatus.PENDING, created.getStatus());
  }

  @Test
  @DisplayName("GET /v1/claims — filter by planName")
  void shouldFilterByPlanName() throws Exception {
    // Given
    createClaimViaApi(
        buildClaimDto(user.getId(), enrollment.getId(), LocalDate.now(), new BigDecimal("10.00")));

    PlanType dental = ensurePlanType("DENTAL", "Dental");
    Plan otherPlan = planRepository.save(
        buildPlan("Vision Premium", dental, new BigDecimal("299.99")));

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

    createClaimViaApi(
        buildClaimDto(user.getId(), otherEnrollment.getId(), LocalDate.now(),
            new BigDecimal("15.00")));

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
        buildClaimDto(user.getId(), enrollment.getId(), LocalDate.now(), new BigDecimal("10.00")));
    ClaimDto mid = createClaimViaApi(
        buildClaimDto(user.getId(), enrollment.getId(), LocalDate.now(), new BigDecimal("20.00")));
    createClaimViaApi(
        buildClaimDto(user.getId(), enrollment.getId(), LocalDate.now(), new BigDecimal("30.00")));

    // When / Then
    mockMvc.perform(get(ENDPOINT)
            .param("amountMin", "20.00")
            .param("amountMax", "20.00")
            .param("page", "0").param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.content[0].id").value(mid.getId().toString()));
  }

  @Test
  @DisplayName("POST /v1/claims/{id}/approval — approves PENDING claim, sets approvedAmount, processedDate, notes")
  void shouldApprovePendingClaim() throws Exception {
    // Given
    ClaimDto created = createClaimViaApi(
        buildClaimDto(user.getId(), enrollment.getId(), LocalDate.now(), new BigDecimal("50.00")));

    var body = objectMapper.createObjectNode()
        .put("approvedAmount", "40.50")
        .put("notes", "ok by reviewer");

    // When
    var result = mockMvc.perform(
            post(ENDPOINT + "/" + created.getId() + "/approval")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("APPROVED"))
        .andExpect(jsonPath("$.approvedAmount").value(40.50))
        .andExpect(jsonPath("$.processedDate").exists())
        .andExpect(jsonPath("$.notes").value("ok by reviewer"))
        .andExpect(jsonPath("$.planName").value(plan.getName()))
        .andExpect(jsonPath("$.processedDate").isNotEmpty())
        .andReturn();

    ClaimDto approved = objectMapper.readValue(result.getResponse().getContentAsString(),
        ClaimDto.class);
    assertEquals(created.getId(), approved.getId());
    assertEquals(ClaimStatus.APPROVED, approved.getStatus());

    // Should fail as it was already approved :P
    mockMvc.perform(
            post(ENDPOINT + "/" + created.getId() + "/approval")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("POST /v1/claims/{id}/approval — 400 when approvedAmount <= 0")
  void shouldRejectInvalidApprovedAmount() throws Exception {
    ClaimDto created = createClaimViaApi(
        buildClaimDto(user.getId(), enrollment.getId(), LocalDate.now(), new BigDecimal("25.00")));

    var badZero = objectMapper.createObjectNode().put("approvedAmount", "0.00");
    mockMvc.perform(
            post(ENDPOINT + "/" + created.getId() + "/approval")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badZero)))
        .andExpect(status().isBadRequest());

    var badNeg = objectMapper.createObjectNode().put("approvedAmount", "-1.00");
    mockMvc.perform(
            post(ENDPOINT + "/" + created.getId() + "/approval")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badNeg)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("POST /v1/claims/{id}/approval — 404 when claim not found")
  void shouldReturnNotFoundOnApproveMissingClaim() throws Exception {
    var body = objectMapper.createObjectNode().put("approvedAmount", "10.00");
    mockMvc.perform(
            post(ENDPOINT + "/" + UUID.randomUUID() + "/approval")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("POST /v1/claims/{id}/denial — denies PENDING claim, sets deniedReason, clears approvedAmount, sets processedDate")
  void shouldDenyPendingClaim() throws Exception {
    // Given
    ClaimDto created = createClaimViaApi(
        buildClaimDto(user.getId(), enrollment.getId(), LocalDate.now(), new BigDecimal("60.00")));

    var body = objectMapper.createObjectNode()
        .put("reason", "Not covered service")
        .put("notes", "explained to member");

    // When
    var result = mockMvc.perform(
            post(ENDPOINT + "/" + created.getId() + "/denial")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("DENIED"))
        .andExpect(jsonPath("$.deniedReason").value("Not covered service"))
        .andExpect(jsonPath(
            "$.approvedAmount").doesNotExist()) // or .value(IsNull.nullValue()) if serialized
        .andExpect(jsonPath("$.processedDate").exists())
        .andExpect(jsonPath("$.notes").value("explained to member"))
        .andExpect(jsonPath("$.processedDate").isNotEmpty())
        .andReturn();

    ClaimDto denied = objectMapper.readValue(result.getResponse().getContentAsString(),
        ClaimDto.class);
    assertEquals(created.getId(), denied.getId());
    assertEquals(ClaimStatus.DENIED, denied.getStatus());

    // Should fail cuz already denied
    mockMvc.perform(
            post(ENDPOINT + "/" + created.getId() + "/denial")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("POST /v1/claims/{id}/denial — 400 when reason is missing/blank")
  void shouldRejectDenialWithoutReason() throws Exception {
    ClaimDto created = createClaimViaApi(
        buildClaimDto(user.getId(), enrollment.getId(), LocalDate.now(), new BigDecimal("15.00")));

    var missing = objectMapper.createObjectNode();
    mockMvc.perform(
            post(ENDPOINT + "/" + created.getId() + "/denial")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(missing)))
        .andExpect(status().isBadRequest());

    var blank = objectMapper.createObjectNode().put("reason", "   ");
    mockMvc.perform(
            post(ENDPOINT + "/" + created.getId() + "/denial")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(blank)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("POST /v1/claims/{id}/denial — 404 when claim not found")
  void shouldReturnNotFoundOnDenyMissingClaim() throws Exception {
    var body = objectMapper.createObjectNode().put("reason", "No claim");
    mockMvc.perform(
            post(ENDPOINT + "/" + UUID.randomUUID() + "/denial")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isNotFound());
  }
  @Test
  @DisplayName("GET /v1/claims/{id} — returns claim by id")
  void shouldGetClaimById() throws Exception {
    // Given
    ClaimDto created = createClaimViaApi(
        buildClaimDto(user.getId(), enrollment.getId(), LocalDate.now(), new BigDecimal("42.00")));

    // When / Then
    mockMvc.perform(get("/v1/claims/{id}", created.getId())
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(created.getId().toString()))
        .andExpect(jsonPath("$.claimNumber").value(created.getClaimNumber()))
        .andExpect(jsonPath("$.status").value("PENDING"))
        .andExpect(jsonPath("$.planName").value(plan.getName()))
        .andExpect(jsonPath("$.consumer.userId").value(user.getId().toString()));
  }

  @Test
  @DisplayName("GET /v1/claims/{id} — 404 when not found")
  void shouldReturnNotFoundWhenClaimMissing() throws Exception {
    // Given
    UUID nonExistentId = UUID.fromString("12345678-0000-0000-0000-000000000000");
    // When / Then
    mockMvc.perform(get("/v1/claims/{id}", nonExistentId)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
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

  private ClaimDto createClaimViaApi(ClaimDto req) throws Exception {
    String response = mockMvc.perform(
            post(ENDPOINT).contentType(MediaType.APPLICATION_JSON)
                .content(createClaimJson(
                    req.getConsumer().getUserId(),
                    req.getEnrollmentId(),
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

  private PlanType ensurePlanType(String code, String name) {
    return planTypeRepository.findByCode(code)
        .orElseGet(() -> planTypeRepository.save(buildPlanType(code, name)));
  }

  private String createClaimJson(
      UUID userId, UUID enrollmentId, LocalDate serviceDate,
      BigDecimal amount, ClaimStatus status, String claimNumber
  ) throws Exception {
    var root = objectMapper.createObjectNode();
    root.put("serviceDate", serviceDate.toString());
    root.put("amount", amount);
    if (userId != null) {
      var consumer = root.putObject("consumer");
      consumer.put("userId", userId.toString());
    }
    if (enrollmentId != null) {
      root.put("enrollmentId", enrollmentId.toString());
    }
    if (status != null) {
      root.put("status", status.name());
    }
    if (claimNumber != null) {
      root.put("claimNumber", claimNumber);
    }
    return objectMapper.writeValueAsString(root);
  }

  private String createClaimJson(UUID userId, UUID enrollmentId, LocalDate serviceDate,
      BigDecimal amount)
      throws Exception {
    return createClaimJson(userId, enrollmentId, serviceDate, amount, null, null);
  }
}
