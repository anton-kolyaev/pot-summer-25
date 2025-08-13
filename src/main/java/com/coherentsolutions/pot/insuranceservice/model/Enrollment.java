package com.coherentsolutions.pot.insuranceservice.model;

import com.coherentsolutions.pot.insuranceservice.model.audit.AuditableSoftDelete;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "enrollments")
public class Enrollment extends AuditableSoftDelete {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "plan_id", nullable = false)
  private Plan plan;

  @NotNull
  @Column(name = "election_amount", nullable = false)
  private BigDecimal electionAmount;

  @NotNull
  @Column(name = "plan_contribution", nullable = false)
  private BigDecimal planContribution;

}
