package com.coherentsolutions.pot.insurance_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
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
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ClaimStatus status = ClaimStatus.PENDING;

    @Column(name = "submitted_date")
    private LocalDateTime submittedDate;

    @Column(name = "processed_date")
    private LocalDateTime processedDate;

    @Column(name = "approved_amount")
    private BigDecimal approvedAmount;

    @Column(name = "denied_reason", columnDefinition = "TEXT")
    private String deniedReason;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreatedBy
    @Column(name = "created_by")
    private UUID createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private UUID updatedBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum ClaimStatus {
        PENDING,
        APPROVED,
        DENIED,
        HOLD
    }
}

