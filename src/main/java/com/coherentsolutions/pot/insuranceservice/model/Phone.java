package com.coherentsolutions.pot.insuranceservice.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a phone contact with a country/area code and number.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Phone {

  @NotBlank(message = "Phone code is required")
  private String code;

  @NotBlank(message = "Phone number is required")
  private String number;
}