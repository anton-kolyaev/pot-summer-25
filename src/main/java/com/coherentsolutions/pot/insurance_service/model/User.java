package com.coherentsolutions.pot.insurance_service.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import com.coherentsolutions.pot.insurance_service.model.Status.UserStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "users")
public class User {
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
    @Column(length = 50, unique = true, nullable = false)
    private String username;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String email;

    @OneToMany
    @JoinColumn(name = "user_id") 
    private List<Phone> phones;

    @OneToMany
    @JoinColumn(name = "user_id")
    private List<Address> addresses;

    @NotNull
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @NotBlank
    @Column(length = 11, unique = true, nullable = false)
    private String ssn;

    // @ManyToOne
    // @JoinColumn(name = "company_id", nullable = false)
    // private Company company;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    private UserFunction function;

    @CreatedBy
    @Column(name = "created_by")
    private UUID createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private UUID updatedBy;

    @CreatedDate
    @Column(name = "created_at",nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
