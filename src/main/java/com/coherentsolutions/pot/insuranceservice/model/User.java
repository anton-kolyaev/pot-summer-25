package com.coherentsolutions.pot.insuranceservice.model;

import com.coherentsolutions.pot.insuranceservice.enums.UserStatus;
import com.coherentsolutions.pot.insuranceservice.model.audit.Auditable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Represents a user within the insurance system. A user is associated with a company. This entity
 * is mapped to the "users" table and includes auditing fields.
 */
@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "users")
public class User extends Auditable {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @NotBlank
  @Column(name = "first_name", length = 100, nullable = false)
  private String firstName;

  @NotBlank
  @Column(name = "last_name", length = 100, nullable = false)
  private String lastName;

  @NotBlank
  @Column(name = "username", length = 50, unique = true, nullable = false)
  private String username;

  @NotBlank
  @Column(name = "email", unique = true, nullable = false)
  private String email;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "address_data", columnDefinition = "jsonb")
  private List<Address> addressData;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "phone_data", columnDefinition = "jsonb")
  private List<Phone> phoneData;

  @NotNull
  @Column(name = "date_of_birth", nullable = false)
  private LocalDate dateOfBirth;

  @NotBlank
  @Column(name = "ssn", length = 11, unique = true, nullable = false)
  private String ssn;

  @ManyToOne
  @JoinColumn(name = "company_id", nullable = false)
  private Company company;

  @Enumerated(EnumType.STRING)
  private UserStatus status;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,
      orphanRemoval = true, fetch = FetchType.EAGER)
  private Set<UserFunctionAssignment> functions;

  @Column(name = "auth0_user_id", length = 255, unique = true)
  private String auth0UserId;
  
}
