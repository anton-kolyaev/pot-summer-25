package com.coherentsolutions.pot.insurance_service.service;

import com.coherentsolutions.pot.insurance_service.dto.AddressDto;
import com.coherentsolutions.pot.insurance_service.model.Address;
import com.coherentsolutions.pot.insurance_service.model.Company;
import com.coherentsolutions.pot.insurance_service.repository.AddressRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {

    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public List<Address> createAddresses(List<AddressDto> addressDtos, Company company) {
        return addressDtos.stream()
                .map(dto -> {
                    Address address = new Address();
                    address.setCountry(dto.getCountry());
                    address.setCity(dto.getCity());
                    address.setState(dto.getState());
                    address.setStreet(dto.getStreet());
                    address.setBuilding(dto.getBuilding());
                    address.setRoom(dto.getRoom());
                    address.setCompany(company);
                    Address savedAddress = addressRepository.save(address);

                    dto.setId(address.getId());
                    return savedAddress;
                })
                .toList();
    }
}