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

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "revinfo_seq")
  @SequenceGenerator(name = "revinfo_seq", sequenceName = "revinfo_seq", allocationSize = 1)
  @Column(name = "id")
  private int id;

  @Column(name = "revtstmp", nullable = false)
  private long timestamp;

  @Column(name = "created_by")
  private UUID createdBy;

  @Column(name = "revision_reason")
  private String revisionReason;


}
