package com.coherentsolutions.pot.insurance_service.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import com.coherentsolutions.pot.insurance_service.model.enums.ClaimStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.EntityListeners;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@Getter
@Setter
@Table(name = "claims")
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(name = "claim_number" ,nullable = false, unique = true,length = 50)
    private String claimNumber;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @NotNull
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @NotBlank
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "submitted_date")
    private LocalDate submittedDate;

    @Column(name = "processed_date")
    private LocalDate processedDate;

    @Column(name = "approved_amount")
    private BigDecimal approvedAmount;

    @Column(name = "denied_reason", columnDefinition = "TEXT")
    private String deniedReason;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

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
    public ClaimStatus getStatus() {
        if(deniedReason != null && !deniedReason.isBlank()) {
            return ClaimStatus.DENIED;
        }
        else if(approvedAmount != null && approvedAmount.compareTo(BigDecimal.ZERO) > 0) {
            return ClaimStatus.APPROVED;
        }
        else if (submittedDate != null && processedDate == null) {
            return ClaimStatus.PENDING;
        }
        else{
            return ClaimStatus.HOLD;
        }

    }
}

