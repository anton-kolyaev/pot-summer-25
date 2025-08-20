package com.coherentsolutions.pot.insuranceservice.model.audit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

/**
 * Custom Envers revision entity. One row per change set. Stores actor's UUID so we know who made each change.
 */

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "revinfo")
@RevisionEntity(RevisionInfoListener.class)
public class RevisionInfo {

  @Id
  @RevisionNumber
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "rev")
  private Integer id;

  @RevisionTimestamp
  @Column(name = "revtstmp", nullable = false)
  private Long timestamp;

  @Column(name = "user_id")
  private UUID userId;
}
