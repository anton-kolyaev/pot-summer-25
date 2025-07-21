package com.coherentsolutions.pot.insuranceservice.model;

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

  private String country;
  private String city;
  private String state;
  private String street;
  private String building;
  private String room;
}