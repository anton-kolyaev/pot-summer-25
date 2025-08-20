package com.coherentsolutions.pot.insuranceservice.model;

import com.coherentsolutions.pot.insuranceservice.enums.ClaimStatus;
import com.coherentsolutions.pot.insuranceservice.model.audit.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "claims")
@Getter
@Setter
@NoArgsConstructor
public class Claim extends Auditable {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "claim_number", nullable = false, length = 100, unique = true, updatable = false)
  private String claimNumber;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private ClaimStatus status;

  @Column(name = "service_date", nullable = false)
  private LocalDate serviceDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User consumer;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "enrollment_id", nullable = false,
      foreignKey = @ForeignKey(name = "fk_claim_enrollment"))
  private Enrollment enrollment;

  @Column(name = "amount", nullable = false)
  private BigDecimal amount;

  @PrePersist
  void assignClaimNumber() {
    if (claimNumber == null || claimNumber.isBlank()) {
      claimNumber = id.toString();
    }
  }

}
