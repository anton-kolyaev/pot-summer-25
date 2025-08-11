package com.coherentsolutions.pot.insuranceservice.model;

import com.coherentsolutions.pot.insuranceservice.enums.PackageStatus;
import com.coherentsolutions.pot.insuranceservice.enums.PayrollFrequency;
import com.coherentsolutions.pot.insuranceservice.model.audit.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@Getter
@Setter
@Table(name = "insurance_packages")
public class InsurancePackage extends Auditable {

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  PackageStatus status;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @NotBlank
  @Column(name = "name", nullable = false)
  private String name;

  @ManyToOne
  @JoinColumn(name = "company_id", nullable = false)
  private Company company;

  @NotNull
  @Column(name = "start_date", nullable = false)
  private LocalDate startDate;

  @NotNull
  @Column(name = "end_date", nullable = false)
  private LocalDate endDate;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "payroll_frequency", nullable = false, length = 20)
  private PayrollFrequency payrollFrequency;

  @ManyToMany
  @JoinTable(
      name = "insurance_package_plans",
      joinColumns = @JoinColumn(name = "insurance_package_id"),
      inverseJoinColumns = @JoinColumn(name = "plan_id")
  )
  private List<Plan> plans;
  
  public void calculateStatus(boolean allowReactivation) {
    if (!allowReactivation && this.status == PackageStatus.DEACTIVATED) {
      return;
    }
    var now = LocalDate.now();
    if (now.isBefore(this.startDate)) {
      this.status = PackageStatus.INITIALIZED;
    } else if (!now.isAfter(this.endDate)) {
      this.status = PackageStatus.ACTIVE;
    } else {
      this.status = PackageStatus.EXPIRED;
    }
  }

}
