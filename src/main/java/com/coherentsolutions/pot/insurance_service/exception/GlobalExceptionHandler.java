package com.coherentsolutions.pot.insurance_service.exception;

import com.coherentsolutions.pot.insurance_service.dto.ErrorDetailsDTO;
import com.coherentsolutions.pot.insurance_service.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler{
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadRequestException(BadRequestException e) {
        ErrorDetailsDTO details = new ErrorDetailsDTO(
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage(),
                e.getDetails()
        );
        return new ResponseEntity<>(new ErrorResponseDTO(details), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<ErrorResponseDTO> handleInsufficientAuthenticationException(InsufficientAuthenticationException e) {
        ErrorDetailsDTO errorDetailsDTO = new ErrorDetailsDTO(
                HttpStatus.UNAUTHORIZED.value(),
                e.getMessage(),
                e.getDetails()
        );
        return new ResponseEntity<>(new ErrorResponseDTO(errorDetailsDTO), HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponseDTO> handleForbiddenException(ForbiddenException e) {
        ErrorDetailsDTO errorDetailsDTO = new ErrorDetailsDTO(
                HttpStatus.FORBIDDEN.value(),
                e.getMessage(),
                e.getDetails()
        );
        return new ResponseEntity<>(new ErrorResponseDTO(errorDetailsDTO), HttpStatus.FORBIDDEN);
    }
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponseDTO> handleConflictException(ConflictException e) {
        ErrorDetailsDTO details = new ErrorDetailsDTO(
                HttpStatus.CONFLICT.value(),
                e.getMessage(),
                e.getDetails()
        );
        return new ResponseEntity<>(new ErrorResponseDTO(details), HttpStatus.CONFLICT);
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFoundException(ResourceNotFoundException e) {
        ErrorDetailsDTO details = new ErrorDetailsDTO(
                HttpStatus.NOT_FOUND.value(),
                e.getMessage(),
                e.getDetails()
        );
        return new ResponseEntity<>(new ErrorResponseDTO(details), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidation(ValidationException e) {
        ErrorDetailsDTO details = new ErrorDetailsDTO(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                e.getMessage(),
                e.getDetails()
        );
        return new ResponseEntity<>(new ErrorResponseDTO(details), HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
