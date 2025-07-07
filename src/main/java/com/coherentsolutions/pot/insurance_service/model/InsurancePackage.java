package com.coherentsolutions.pot.insurance_service.model;

import com.coherentsolutions.pot.insurance_service.model.enums.PackageStatus;
import com.coherentsolutions.pot.insurance_service.model.enums.PayrollFrequency;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "insurance_packages")
public class InsurancePackage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(nullable = false)
    private String name;

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

    @OneToMany(mappedBy = "insurancePackage")
    private List<Plan> plans;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PackageStatus status;

    @CreatedBy
    @Column(name = "created_by")
    private UUID createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private UUID updatedBy;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Transient
    public PackageStatus getStatus() {
        if(endDate != null && LocalDate.now().isAfter(endDate)) {
            return PackageStatus.DEACTIVATED;
        }
        else if(startDate != null && LocalDate.now().isAfter(startDate) && LocalDate.now().isBefore(endDate)) {
            return PackageStatus.ACTIVE;
        }
        else {
            return PackageStatus.INITIALIZED;
        }
    }


}

