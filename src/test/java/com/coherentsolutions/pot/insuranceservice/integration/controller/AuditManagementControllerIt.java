package com.coherentsolutions.pot.insuranceservice.integration.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.coherentsolutions.pot.insuranceservice.dto.company.CompanyDto;
import com.coherentsolutions.pot.insuranceservice.integration.IntegrationTestConfiguration;
import com.coherentsolutions.pot.insuranceservice.integration.TestSecurityUtils;
import com.coherentsolutions.pot.insuranceservice.integration.containers.PostgresTestContainer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Import(IntegrationTestConfiguration.class)
class AuditManagementControllerIt extends PostgresTestContainer {

  private static final UUID TEST_USER =
      UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

  @Autowired
  MockMvc mockMvc;
  @Autowired
  ObjectMapper objectMapper;

  @Test
  @DisplayName("Company history: ADD then MOD with correct actor")
  void history_addThenMod_withActor() throws Exception {
    // Given
    CompanyDto created = createCompany(companyDtoBuilder("Acme").build());
    UUID id = created.getId();
    assertNotNull(id);

    // When
    updateCompany(id, companyDtoBuilder("Acme Renamed").build());

    // Then
    CompanyDto now = getCompany(id);
    assertEquals("Acme Renamed", now.getName());
    assertEquals(TEST_USER, now.getUpdatedBy());

    List<HistoryItem> history = getCompanyHistory(id);
    assertEquals(2, history.size());

    assertEquals("ADD", history.get(0).type());
    assertEquals("MOD", history.get(1).type());
    assertEquals(TEST_USER.toString(), history.get(0).actor());
    assertEquals(TEST_USER.toString(), history.get(1).actor());

    Instant t0 = Instant.parse(history.get(0).at());
    Instant t1 = Instant.parse(history.get(1).at());
    assertFalse(t0.isAfter(t1));
  }

  @Test
  @DisplayName("No-op update: identical payload does NOT create a new revision")
  void history_noopUpdate_doesNotGrow() throws Exception {
    // Given
    CompanyDto base = companyDtoBuilder("NoopCo").build();
    CompanyDto created = createCompany(base);
    UUID id = created.getId();

    List<HistoryItem> before = getCompanyHistory(id);
    assertEquals(1, before.size());

    CompanyDto fetchedBefore = getCompany(id);
    Instant updatedAtBefore = Instant.parse(fetchedBefore.getUpdatedAt().toString());

    // When
    updateCompany(id, base);

    // Then
    List<HistoryItem> after = getCompanyHistory(id);
    assertEquals(1, after.size());

    CompanyDto fetchedAfter = getCompany(id);
    Instant updatedAtAfter = Instant.parse(fetchedAfter.getUpdatedAt().toString());
    assertEquals(updatedAtBefore, updatedAtAfter);
  }

  @Test
  @DisplayName("Multiple updates: ADD + 3×MOD, correct order and final state")
  void history_multipleMods_orderAndCount() throws Exception {
    // Given
    CompanyDto created = createCompany(companyDtoBuilder("RevCo v0").build());
    UUID id = created.getId();

    // When
    updateCompany(id, companyDtoBuilder("RevCo v1").build());
    updateCompany(id, companyDtoBuilder("RevCo v2").build());
    updateCompany(id, companyDtoBuilder("RevCo v3").build());

    // Then
    List<HistoryItem> history = getCompanyHistory(id);
    assertEquals(4, history.size());

    assertEquals("ADD", history.get(0).type());
    assertEquals("MOD", history.get(1).type());
    assertEquals("MOD", history.get(2).type());
    assertEquals("MOD", history.get(3).type());

    Instant t0 = Instant.parse(history.get(0).at());
    Instant t1 = Instant.parse(history.get(1).at());
    Instant t2 = Instant.parse(history.get(2).at());
    Instant t3 = Instant.parse(history.get(3).at());
    assertTrue(!t0.isAfter(t1) && !t1.isAfter(t2) && !t2.isAfter(t3));

    CompanyDto now = getCompany(id);
    assertEquals("RevCo v3", now.getName());
    assertEquals(TEST_USER, now.getUpdatedBy());
  }

  @Test
  @DisplayName("Unknown company: history endpoint returns empty array")
  void history_unknownCompany_returnsEmpty() throws Exception {
    // Given
    UUID unknown = UUID.randomUUID();

    // When
    List<HistoryItem> history = getCompanyHistory(unknown);

    // Then
    assertNotNull(history);
    assertEquals(0, history.size());
  }

  private CompanyDto.CompanyDtoBuilder companyDtoBuilder(String name) {
    return CompanyDto.builder()
        .name(name)
        .countryCode("123")
        .email("test@company.com");
  }

  private CompanyDto createCompany(CompanyDto dto) throws Exception {
    String json =
        mockMvc
            .perform(
                post("/v1/companies")
                    .with(TestSecurityUtils.adminUser())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
    return objectMapper.readValue(json, CompanyDto.class);
  }

  private CompanyDto updateCompany(UUID id, CompanyDto dto) throws Exception {
    String json =
        mockMvc
            .perform(
                put("/v1/companies/{id}", id)
                    .with(TestSecurityUtils.adminUser())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    return objectMapper.readValue(json, CompanyDto.class);
  }

  private CompanyDto getCompany(UUID id) throws Exception {
    String json =
        mockMvc
            .perform(get("/v1/companies/{id}", id).with(TestSecurityUtils.adminUser()))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    return objectMapper.readValue(json, CompanyDto.class);
  }

  private List<HistoryItem> getCompanyHistory(UUID id) throws Exception {
    String json =
        mockMvc
            .perform(get("/v1/companies/{id}/history", id).with(TestSecurityUtils.adminUser()))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    JsonNode arr = objectMapper.readTree(json);
    return objectMapper.readValue(arr.toString(), new TypeReference<List<HistoryItem>>() {
    });
  }

  private record HistoryItem(Integer revision, String at, String actor, String type) {

  }
}
