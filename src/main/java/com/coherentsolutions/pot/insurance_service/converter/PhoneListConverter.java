package com.coherentsolutions.pot.insurance_service.converter;

import com.coherentsolutions.pot.insurance_service.model.Phone;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Collections;
import java.util.List;

@Converter
public class PhoneListConverter implements AttributeConverter<List<Phone>,String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Phone> phones) {
        if(phones == null|| phones.isEmpty()){
            return "[]";
        }
        try{
            return objectMapper.writeValueAsString(phones);
        }
        catch(Exception e){
            throw new IllegalArgumentException("Error converting Phone list into JSON", e);
        }

    }

    @Override
    public List<Phone> convertToEntityAttribute(String json) {
        if(json == null || json.isEmpty()){
            return Collections.emptyList();
        }
        try{
            return objectMapper.readValue(json, new TypeReference<List<Phone>>(){});
        }
        catch(Exception e){
            throw new IllegalArgumentException("Error converting Phone list from JSON", e);
        }
    }

}