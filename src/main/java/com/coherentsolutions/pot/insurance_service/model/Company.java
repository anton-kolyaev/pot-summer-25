package com.coherentsolutions.pot.insurance_service.model;


import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.coherentsolutions.pot.insurance_service.converter.AddressListConverter;
import com.coherentsolutions.pot.insurance_service.converter.PhoneListConverter;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import com.coherentsolutions.pot.insurance_service.model.enums.CompanyStatus;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@Getter
@Setter
@Table(name = "companies")
public class Company {
    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    private UUID id;

    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank
    @Column(name = "country_code", nullable = false, length = 3)
    private String countryCode;

    @Convert(converter = AddressListConverter.class)
    @Column(columnDefinition = "json")
    private List<Address> addresses;

    @Convert(converter = PhoneListConverter.class)
    @Column(columnDefinition = "json")
    private List<Phone> phones;

    @Email
    private String email;

    private String website;

    @OneToMany
    @JoinColumn(name = "company_id")
    private List<InsurancePackage> insurancePackages;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private CompanyStatus status;

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

