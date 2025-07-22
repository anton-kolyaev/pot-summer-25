package com.coherentsolutions.pot.insuranceservice.util;

import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Utility class for common object-related helper methods.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ObjectUtils {

  /**
   * Executes the given setter function if the provided value is not null.
   */
  public static <T> void setIfNotNull(T value, Consumer<T> setter) {
    if (value != null) {
      setter.accept(value);
    }
  }
}
