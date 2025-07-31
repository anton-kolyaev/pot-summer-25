package com.coherentsolutions.pot.insuranceservice.integration.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.coherentsolutions.pot.insuranceservice.dto.company.CompanyDto;
import com.coherentsolutions.pot.insuranceservice.integration.IntegrationTestConfiguration;
import com.coherentsolutions.pot.insuranceservice.integration.containers.PostgresTestContainer;
import com.coherentsolutions.pot.insuranceservice.model.Company;
import com.coherentsolutions.pot.insuranceservice.repository.CompanyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
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
class AuditConfigIt extends PostgresTestContainer {

  private static final UUID TEST_USER =
      UUID.fromString("11111111-2222-3333-4444-555555555555");
  
  @Autowired
  MockMvc mockMvc;
  @Autowired
  ObjectMapper objectMapper;
  @Autowired
  CompanyRepository companies;

  @AfterEach
  void cleanUp() {
    companies.deleteAllInBatch();
  }

  private CompanyDto companyBuilder(String name) {
    return CompanyDto.builder()
        .name(name)
        .countryCode("123")
        .email("test@company.com")
        .build();
  }

  @Test
  @DisplayName("createdBy / createdAt are set on POST /v1/companies")
  void audit_onPost() throws Exception {

    CompanyDto testCompany = companyBuilder("TestCompany");

    mockMvc.perform(post("/v1/companies")
            .with(jwt().jwt(j -> j.subject(TEST_USER.toString())))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testCompany)))
        .andExpect(status().isCreated());

    Company saved = companies.findAll().getFirst();

    assertThat(saved.getCreatedBy()).isEqualTo(TEST_USER);
    assertThat(saved.getUpdatedBy()).isEqualTo(TEST_USER);
    assertThat(saved.getCreatedAt()).isNotNull();
    assertThat(saved.getUpdatedAt()).isEqualTo(saved.getCreatedAt());
  }

  @Test
  @DisplayName("updatedBy / updatedAt change on PUT")
  void audit_onPut() throws Exception {

    CompanyDto testCompanyBefore = companyBuilder("Before");

    mockMvc.perform(post("/v1/companies")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testCompanyBefore)))
        .andExpect(status().isCreated());

    Company firstTestCompany = companies.findAll().getFirst();
    UUID id = firstTestCompany.getId();
    Instant firstUpd = firstTestCompany.getUpdatedAt();

    CompanyDto testCompanyAfter = companyBuilder("After");

    mockMvc.perform(put("/v1/companies/{id}", id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testCompanyAfter)))
        .andExpect(status().isOk());

    Company updated = companies.findById(id).orElseThrow();

    assertThat(updated.getUpdatedBy()).isEqualTo(TEST_USER);
    assertThat(updated.getUpdatedAt()).isAfter(firstUpd);
  }

  @Test
  @DisplayName("No-op PUT must NOT touch updatedAt / updatedBy")
  void audit_noOpPutMustNotTouchAuditColumns() throws Exception {

    CompanyDto testCompany = companyBuilder("testCompany");
    mockMvc.perform(post("/v1/companies")
            .with(jwt().jwt(j -> j.subject(TEST_USER.toString())))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testCompany)))
        .andExpect(status().isCreated());

    Company testCompanyBefore = companies.findAll().getFirst();
    UUID id = testCompanyBefore.getId();
    Instant tsBefore = testCompanyBefore.getUpdatedAt();

    mockMvc.perform(put("/v1/companies/{id}", id)
            .with(jwt().jwt(j -> j.subject(TEST_USER.toString())))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testCompany)))
        .andExpect(status().isOk());

    Company testCompanyAfter = companies.findById(id).orElseThrow();

    assertThat(testCompanyAfter.getUpdatedAt()).isEqualTo(tsBefore);

    assertThat(testCompanyAfter.getUpdatedBy()).isEqualTo(TEST_USER);
  }

}
