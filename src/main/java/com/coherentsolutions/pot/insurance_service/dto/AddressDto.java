package com.coherentsolutions.pot.insurance_service.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AddressDto {
    private UUID id;
    private String country;
    private String city;
    private String state;
    private String street;
    private String building;
    private String room;
}
