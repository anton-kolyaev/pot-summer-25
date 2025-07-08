package com.coherentsolutions.pot.insurance_service.service;

import com.coherentsolutions.pot.insurance_service.dto.PhoneDto;
import com.coherentsolutions.pot.insurance_service.model.Company;
import com.coherentsolutions.pot.insurance_service.model.Phone;
import com.coherentsolutions.pot.insurance_service.repository.PhoneRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhoneService {
    private final PhoneRepository phoneRepository;
    public PhoneService(PhoneRepository phoneRepository) {
        this.phoneRepository = phoneRepository;
    }

    public List<Phone> createPhones(List<PhoneDto> phoneDtos, Company company) {
        return phoneDtos.stream()
                .map(dto -> {
                    Phone phone = new Phone();
                    phone.setCode(dto.getCode());
                    phone.setNumber(dto.getNumber());
                    phone.setCompany(company);
                    Phone savedPhone = phoneRepository.save(phone);

                    dto.setId(phone.getId());

                    return savedPhone;
                })
                .toList();
    }
}
