package com.coherentsolutions.pot.insuranceservice.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.coherentsolutions.pot.insuranceservice.enums.UserStatus;
import com.coherentsolutions.pot.insuranceservice.model.User;

/**
 * Repository interface for accessing and managing {@link User} entities.
 * Extends {@link JpaRepository} for basic CRUD operations and
 * {@link JpaSpecificationExecutor} for filtering capabilities.
 */
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    /**
     * Finds all users belonging to a specific company.
     */
    List<User> findByCompanyId(UUID companyId);

    default User getByIdOrThrow(UUID id) {
        return findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    /**
     * Updates the status of all users that belong to a specific company.
     */
    @Modifying
    @Query("UPDATE User u SET u.status = :status WHERE u.company.id = :companyId")
    void updateUserStatusByCompanyId(@Param("companyId") UUID companyId,
            @Param("status") UserStatus status);

    /**
     * Updates the status of users with the given list of user IDs.
     */
    @Modifying
    @Query("UPDATE User u SET u.status = :status WHERE u.id IN :userIds")
    void updateUserStatusByIds(@Param("userIds") List<UUID> userIds,
            @Param("status") UserStatus status);
}
