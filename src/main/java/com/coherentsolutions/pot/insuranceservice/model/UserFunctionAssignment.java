package com.coherentsolutions.pot.insuranceservice.model;

import com.coherentsolutions.pot.insuranceservice.enums.UserFunction;
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
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing the assignment of a {@link UserFunction} to a {@link User}. This allows users
 * to have multiple functional roles within the system. Mapped to the "user_functions" table.
 */
@Entity
@Table(name = "user_functions")
@NoArgsConstructor
@Getter
@Setter
public class UserFunctionAssignment {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UserFunction function;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
}
