package com.coherentsolutions.pot.insuranceservice.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Represents a physical address.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address {

  @NotBlank(message = "Country is required")
  private String country;

  @NotBlank(message = "City is required")
  private String city;

  @NotBlank(message = "State is required")
  private String state;

  @NotBlank(message = "Street is required")
  private String street;

  @NotBlank(message = "Building is required")
  private String building;
  private String room;
}