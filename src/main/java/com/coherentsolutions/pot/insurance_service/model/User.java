package com.coherentsolutions.pot.insurance_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


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

    @OneToMany(mappedBy = "user")
    private List<Phone> phones;

    @NotNull
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @NotBlank
    @Column(length = 11, unique = true, nullable = false)
    private String ssn;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @CreatedBy
    @Column(name = "created_by")
    private UUID createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private UUID updatedBy;


    @CreationTimestamp
    @Column(name = "created_at",nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    public enum Status{
        ACTIVE,
        INACTIVE
    }
}

