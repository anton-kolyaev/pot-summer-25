package com.coherentsolutions.pot.insurance_service.converter;

import com.coherentsolutions.pot.insurance_service.model.Address;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Collections;
import java.util.List;

@Converter
public class AddressListConverter implements AttributeConverter<List<Address>, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Address> addresses) {
        if(addresses == null || addresses.isEmpty()) {
            return "[]";
        }
        try{
            return objectMapper.writeValueAsString(addresses);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Error converting Address list to JSON", e);
        }
    }

    @Override
    public List<Address> convertToEntityAttribute(String json) {
        if(json == null || json.isEmpty()) {
            return Collections.emptyList();
        }
        try{
            return objectMapper.readValue(json, new TypeReference<List<Address>>(){});
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Error converting Address list from JSON", e);
        }
    }
}
