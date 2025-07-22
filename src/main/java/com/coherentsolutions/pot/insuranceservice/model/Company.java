package com.coherentsolutions.pot.insuranceservice.model;


import com.coherentsolutions.pot.insuranceservice.enums.CompanyStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Represents a company entity in the insurance service domain.
 * Mapped to the "companies" table in the database.
 */
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

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "address_data", columnDefinition = "jsonb")
  private List<Address> addressData;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "phone_data", columnDefinition = "jsonb")
  private List<Phone> phoneData;

  @Email
  private String email;

  private String website;

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
