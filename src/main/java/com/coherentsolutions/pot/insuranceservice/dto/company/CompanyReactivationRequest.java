package com.coherentsolutions.pot.insuranceservice.dto.company;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request object for reactivating a company.
 *
 * <p>Includes options for user reactivation during the company reactivation process.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyReactivationRequest {

  private UserReactivationOption userReactivationOption = UserReactivationOption.NONE;
  private List<UUID> selectedUserIds;

  /**
   * Enumeration of user reactivation options.
   */
  public enum UserReactivationOption {
    ALL,
    NONE,
    SELECTED
  }
} 