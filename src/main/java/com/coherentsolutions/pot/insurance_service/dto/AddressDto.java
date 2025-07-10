package com.coherentsolutions.pot.insurance_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressDto {
    private String country;
    private String city;
    private String state;
    private String street;
    private String building;
    private String room;
}
