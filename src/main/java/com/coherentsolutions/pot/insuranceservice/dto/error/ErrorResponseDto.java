package com.coherentsolutions.pot.insuranceservice.dto.error;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing an error response.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.WRAPPER_OBJECT
)
@JsonTypeName("error")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDto {

  private String code;
  private String message;
  private Object details;
}
