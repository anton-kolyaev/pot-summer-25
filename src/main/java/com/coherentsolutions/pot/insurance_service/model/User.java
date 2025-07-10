package com.coherentsolutions.pot.insurance_service.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.coherentsolutions.pot.insurance_service.converter.AddressListConverter;
import com.coherentsolutions.pot.insurance_service.converter.PhoneListConverter;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import com.coherentsolutions.pot.insurance_service.model.enums.Status;

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
    @Column(name = "username", length = 50, unique = true, nullable = false)
    private String username;

    @NotBlank
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Convert(converter = AddressListConverter.class)
    @Column(columnDefinition = "json")
    private List<Address> addresses;

    @Convert(converter = PhoneListConverter.class)
    @Column(columnDefinition = "json")
    private List<Phone> phones;

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
    private Status status;

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

