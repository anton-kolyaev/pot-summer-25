package com.coherentsolutions.pot.insuranceservice.service;

import com.coherentsolutions.pot.insuranceservice.model.audit.RevisionInfo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuditHistoryService {

  @PersistenceContext
  private EntityManager entityManager;

  public List<RevisionMetadata> userHistory(UUID userId) {
    return historyForEntity(com.coherentsolutions.pot.insuranceservice.model.User.class, userId);
  }

  public List<RevisionMetadata> companyHistory(UUID companyId) {
    return historyForEntity(com.coherentsolutions.pot.insuranceservice.model.Company.class,
        companyId);
  }

  private <T> List<RevisionMetadata> historyForEntity(Class<T> entityClass, UUID id) {
    AuditReader reader = AuditReaderFactory.get(entityManager);
    @SuppressWarnings("unchecked")
    List<Object[]> rows = reader.createQuery()
        .forRevisionsOfEntity(entityClass, false,
            true)
        .add(AuditEntity.id().eq(id))
        .addOrder(AuditEntity.revisionNumber().asc())
        .getResultList();

    return rows.stream().map(r -> {
      RevisionInfo rev = (RevisionInfo) r[1];
      RevisionType type = (RevisionType) r[2];
      return new RevisionMetadata(
          rev.getId(),
          Instant.ofEpochMilli(rev.getTimestamp()),
          rev.getUserId(),
          type.name()
      );
    }).toList();
  }

  public record RevisionMetadata(Integer revision, Instant at, UUID actor, String type) {

  }
}
