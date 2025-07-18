package com.coherentsolutions.pot.insurance_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.coherentsolutions.pot.insurance_service.model.User;
import com.coherentsolutions.pot.insurance_service.enums.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User>{ 
    
    List<User> findByCompanyId(UUID companyId);
    
    @Modifying
    @Query("UPDATE User u SET u.status = :status WHERE u.company.id = :companyId")
    void updateUserStatusByCompanyId(@Param("companyId") UUID companyId, @Param("status") UserStatus status);
    
    @Modifying
    @Query("UPDATE User u SET u.status = :status WHERE u.id IN :userIds")
    void updateUserStatusByIds(@Param("userIds") List<UUID> userIds, @Param("status") UserStatus status);
}
