package com.coherentsolutions.pot.insurance_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.coherentsolutions.pot.insurance_service.model.User;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> { 

}
