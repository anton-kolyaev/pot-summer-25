package com.coherentsolutions.pot.insurance_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler{
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException e) {
        ErrorDetails details = new ErrorDetails(
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage(),
                e.getDetails()
        );
        return new ResponseEntity<>(new ErrorResponse(details), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientAuthentication(InsufficientAuthenticationException e) {
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.UNAUTHORIZED.value(),
                e.getMessage(),
                e.getDetails()
        );
        return new ResponseEntity<>(new ErrorResponse(errorDetails), HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException e) {
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.NOT_FOUND.value(),
                e.getMessage(),
                e.getDetails()
        );
        return new ResponseEntity<>(new ErrorResponse(errorDetails), HttpStatus.NOT_FOUND);
    }
    
}
