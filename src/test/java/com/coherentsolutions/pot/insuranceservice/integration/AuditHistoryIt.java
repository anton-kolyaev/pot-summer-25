package com.coherentsolutions.pot.insuranceservice.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.coherentsolutions.pot.insuranceservice.dto.company.CompanyDto;
import com.coherentsolutions.pot.insuranceservice.integration.containers.PostgresTestContainer;
import com.coherentsolutions.pot.insuranceservice.model.Company;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(IntegrationTestConfiguration.class)
@DisplayName("Integration test for auditing history with Envers (via controller)")
class AuditHistoryIt extends PostgresTestContainer {

  private static final Logger log = LoggerFactory.getLogger(AuditHistoryIt.class);

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private EntityManager entityManager;

  @Autowired
  private PlatformTransactionManager transactionManager;

  @Commit
  @Test
  void envers_shouldTrackAuditRevisions() throws Exception {
    // 1. Create company
    CompanyDto createDto = new CompanyDto();
    createDto.setName("Acme Inc");
    createDto.setCountryCode("US");
    createDto.setEmail("info@acme.com");

    MvcResult result = mockMvc.perform(post("/v1/companies")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createDto)))
        .andExpect(status().isCreated())
        .andReturn();

    final UUID companyId = objectMapper.readValue(
        result.getResponse().getContentAsString(), CompanyDto.class).getId();

    // Flush and clear to ensure creation is persisted
    entityManager.flush();
    entityManager.clear();

    // 2. Update company
    CompanyDto updateDto = new CompanyDto();
    updateDto.setName("Acme Corp");
    updateDto.setCountryCode("US");
    updateDto.setEmail("info@acme.com");

    mockMvc.perform(put("/v1/companies/" + companyId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isOk());

    // Flush and clear again
    entityManager.flush();
    entityManager.clear();
    TestTransaction.flagForCommit();
    TestTransaction.end();

    // 3. Verify audit history in NEW transaction
    TestTransaction.start();
    verifyAuditHistory(companyId);
  }

  private void verifyAuditHistory(UUID companyId) {
    TransactionTemplate txTemplate = new TransactionTemplate(transactionManager);
    txTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

    txTemplate.execute(status -> {
      AuditReader reader = AuditReaderFactory.get(entityManager);

      // First check if entity is audited
      boolean isAudited = reader.isEntityClassAudited(Company.class);
      assertTrue(isAudited, "Company entity should be audited");

      // Verify revisions
      List<Number> revisions = reader.getRevisions(Company.class, companyId);
      log.info("Found revisions: {}", revisions);

      if (revisions.isEmpty()) {
        // Debug why no revisions exist
        logAuditTables();
      }

      assertEquals(2, revisions.size(), "There should be two revisions");

      // Verify revision details
      List<?> rawResult = reader.createQuery()
          .forRevisionsOfEntity(Company.class, false, true)
          .add(AuditEntity.id().eq(companyId))
          .getResultList();

      List<Object[]> history = rawResult.stream()
          .map(o -> (Object[]) o)
          .toList();

      assertEquals(2, history.size(), "Should have 2 history entries");

      Company rev1 = (Company) history.get(0)[0];
      RevisionType type1 = (RevisionType) history.get(0)[2];

      Company rev2 = (Company) history.get(1)[0];

      assertEquals("Acme Inc", rev1.getName());
      assertEquals("Acme Corp", rev2.getName());
      assertEquals(RevisionType.ADD, type1);

      RevisionType type2 = (RevisionType) history.get(1)[2];
      assertEquals(RevisionType.MOD, type2);

      return null;
    });
  }

  private void logAuditTables() {
    try {
      // Check REVINFO table
      List revInfo = entityManager.createNativeQuery("SELECT * FROM revinfo").getResultList();
      log.info("REVINFO table has {} rows", revInfo.size());

      // Check COMPANIES_AUD table
      List companiesAud = entityManager.createNativeQuery("SELECT * FROM companies_aud")
          .getResultList();
      log.info("Companies_AUD table has {} rows", companiesAud.size());

      // Check if tables exist
      List<?> rawResult = entityManager.createNativeQuery(
          "SELECT table_name FROM information_schema.tables WHERE table_name IN ('revinfo', 'companies_aud')"
      ).getResultList();

      List<Object[]> tables = rawResult.stream()
          .map(row -> (Object[]) row)
          .collect(Collectors.toList());
      log.info("Existing audit tables: {}", tables);
    } catch (Exception e) {
      log.error("Error checking audit tables", e);
    }
  }
}
