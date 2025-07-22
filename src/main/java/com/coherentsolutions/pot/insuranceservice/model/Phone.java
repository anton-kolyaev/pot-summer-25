package com.coherentsolutions.pot.insuranceservice.model;

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

  private String code;
  private String number;
}