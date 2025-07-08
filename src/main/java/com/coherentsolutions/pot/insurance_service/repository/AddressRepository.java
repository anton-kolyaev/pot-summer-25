package com.coherentsolutions.pot.insurance_service.repository;

import com.coherentsolutions.pot.insurance_service.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {
}
