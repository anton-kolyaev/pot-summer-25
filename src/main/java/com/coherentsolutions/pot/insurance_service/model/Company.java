package com.coherentsolutions.pot.insurance_service.model;


import com.coherentsolutions.pot.insurance_service.model.enums.CompanyStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;


import java.time.Instant;
import java.util.List;
import java.util.UUID;


@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "companies")
public class Company {
    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    private UUID id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(name = "country_code", nullable = false, length = 3)
    private String countryCode;

    @NotEmpty
    @OneToMany(mappedBy = "company")
    @Size(min = 1)
    private List<Address> addresses;

    @NotEmpty
    @OneToMany(mappedBy = "company")
    @Size(min = 1)
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

