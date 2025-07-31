package com.coherentsolutions.pot.insuranceservice.exception;

/**
 * Custom exception for Auth0-related errors.
 *
 * <p>This exception provides a standardized way to handle Auth0-specific errors
 * and includes additional context for debugging and logging.
 */
public class Auth0Exception extends RuntimeException {

  private final String errorCode;
  private final int httpStatus;

  /**
   * Creates a new Auth0Exception with the specified message.
   *
   * @param message the error message
   */
  public Auth0Exception(String message) {
    super(message);
    this.errorCode = "AUTH0_ERROR";
    this.httpStatus = 500;
  }

  /**
   * Creates a new Auth0Exception with the specified message and cause.
   *
   * @param message the error message
   * @param cause the cause of the exception
   */
  public Auth0Exception(String message, Throwable cause) {
    super(message, cause);
    this.errorCode = "AUTH0_ERROR";
    this.httpStatus = 500;
  }

  /**
   * Creates a new Auth0Exception with the specified message, error code, and HTTP status.
   *
   * @param message the error message
   * @param errorCode the error code
   * @param httpStatus the HTTP status code
   */
  public Auth0Exception(String message, String errorCode, int httpStatus) {
    super(message);
    this.errorCode = errorCode;
    this.httpStatus = httpStatus;
  }

  /**
   * Creates a new Auth0Exception with the specified message, cause, error code, and HTTP status.
   *
   * @param message the error message
   * @param cause the cause of the exception
   * @param errorCode the error code
   * @param httpStatus the HTTP status code
   */
  public Auth0Exception(String message, Throwable cause, String errorCode, int httpStatus) {
    super(message, cause);
    this.errorCode = errorCode;
    this.httpStatus = httpStatus;
  }

  /**
   * Gets the error code.
   *
   * @return the error code
   */
  public String getErrorCode() {
    return errorCode;
  }

  /**
   * Gets the HTTP status code.
   *
   * @return the HTTP status code
   */
  public int getHttpStatus() {
    return httpStatus;
  }
} 