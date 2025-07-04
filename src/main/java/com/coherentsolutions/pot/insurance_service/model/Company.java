package com.coherentsolutions.pot.insurance_service.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import java.time.LocalDateTime;
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

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "who_created_id")
    private User whoCreated;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum Status {
        ACTIVE,
        INACTIVE
    }


}
