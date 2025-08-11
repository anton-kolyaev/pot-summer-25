package com.coherentsolutions.pot.insuranceservice.model;

import com.coherentsolutions.pot.insuranceservice.model.audit.AuditableSoftDelete;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

@Entity
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE plans SET deleted_at = now() WHERE id = ?")
@Table(name = "plans")
public class Plan extends AuditableSoftDelete {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "name", length = 100, nullable = false)
  private String name;

  @OneToOne
  @JoinColumn(name = "plan_type_id", nullable = false)
  private PlanType type;

  @Column(name = "contribution", nullable = false)
  private BigDecimal contribution;

  @ManyToMany(mappedBy = "plans")
  private List<InsurancePackage> insurancePackages;

}
