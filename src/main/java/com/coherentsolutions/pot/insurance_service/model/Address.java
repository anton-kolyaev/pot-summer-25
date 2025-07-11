package com.coherentsolutions.pot.insurance_service.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Address {
    private String country;
    private String city;
    private String state;
    private String street;
    private String building;
    private String room;
}