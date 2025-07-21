package com.coherentsolutions.pot.insurance_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyReactivationRequest {
    public enum UserReactivationOption {
        ALL,
        NONE,
        SELECTED
    }
    
    private UserReactivationOption userReactivationOption = UserReactivationOption.NONE;
    private List<UUID> selectedUserIds;
} 