package com.coherentsolutions.pot.insurance_service.repository;

import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.coherentsolutions.pot.insurance_service.model.User;
import com.coherentsolutions.pot.insurance_service.enums.UserStatus;

public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    
    List<User> findByCompanyId(UUID companyId);
    
    default User getByIdOrThrow(UUID id) {
        return findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
    
    @Modifying
    @Query("UPDATE User u SET u.status = :status WHERE u.company.id = :companyId")
    void updateUserStatusByCompanyId(@Param("companyId") UUID companyId, @Param("status") UserStatus status);
    
    @Modifying
    @Query("UPDATE User u SET u.status = :status WHERE u.id IN :userIds")
    void updateUserStatusByIds(@Param("userIds") List<UUID> userIds, @Param("status") UserStatus status);
}
