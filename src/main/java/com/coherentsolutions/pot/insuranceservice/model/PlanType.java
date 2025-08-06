package com.coherentsolutions.pot.insuranceservice.model;


import com.coherentsolutions.pot.insuranceservice.model.audit.CreationAudit;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "plan_types")
@Getter
@Setter
@NoArgsConstructor
public class PlanType extends CreationAudit {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "code", nullable = false, unique = true)
  private String code;

  @Column(name = "name", nullable = false)
  private String name;
  
}
