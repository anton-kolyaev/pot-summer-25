package com.coherentsolutions.pot.insuranceservice;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;


@Entity
@RevisionEntity(CustomRevisionListener.class)
@Table(name = "revinfo")
@Getter
@Setter
public class CustomRevisionEntity extends DefaultRevisionEntity {

  @Column(name = "timestamp")
  private long timestamp;

  @Column(name = "created_by")
  private UUID createdBy;

  @Column(name = "revision_reason")
  private String revisionReason;


}
