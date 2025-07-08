package com.coherentsolutions.pot.insurance_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_functions")
@NoArgsConstructor
@Getter
@Setter
public class UserFunctionAssignment {
    
    @Id
    @GeneratedValue
    private Integer id;  

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserFunction function;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
