package com.coherentsolutions.pot.insuranceservice.model.audit;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@MappedSuperclass
public abstract class Auditable extends CreationAudit {

  @LastModifiedBy
  @Column(name = "updated_by", nullable = false)
  private String updatedBy;
  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;
}
