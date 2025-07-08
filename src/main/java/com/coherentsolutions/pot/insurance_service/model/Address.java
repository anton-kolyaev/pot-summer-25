package com.coherentsolutions.pot.insurance_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.GenerationType;
import jakarta.persistence.EntityListeners;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@Getter
@Setter
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(nullable = false)
    private String country;

    @NotBlank
    @Column(nullable = false)
    private String city;

    private String state;

    @NotBlank
    @Column(nullable = false)
    private String street;

    @NotBlank
    @Column(nullable = false)
    private String building;

    private String room;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

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
}

