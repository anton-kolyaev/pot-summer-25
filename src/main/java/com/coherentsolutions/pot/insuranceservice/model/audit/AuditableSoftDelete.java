package com.coherentsolutions.pot.insuranceservice.model.audit;


import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import lombok.Getter;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.SoftDelete;

@Getter
@SQLRestriction("deleted_at IS NULL")
@MappedSuperclass
public class AuditableSoftDelete extends Auditable {

  @SoftDelete
  @Column(name = "deleted_at")
  private Instant deletedAt;
}
