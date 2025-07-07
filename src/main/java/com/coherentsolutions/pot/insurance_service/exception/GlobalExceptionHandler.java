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
                HttpStatus.BAD_REQUEST.name(),
                e.getMessage(),
                e.getDetails()
        );
        return new ResponseEntity<>(new ErrorResponseDTO(details), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<ErrorResponseDTO> handleInsufficientAuthenticationException(InsufficientAuthenticationException e) {
        ErrorDetailsDTO errorDetailsDTO = new ErrorDetailsDTO(
                HttpStatus.UNAUTHORIZED.name(),
                e.getMessage(),
                e.getDetails()
        );
        return new ResponseEntity<>(new ErrorResponseDTO(errorDetailsDTO), HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponseDTO> handleForbiddenException(ForbiddenException e) {
        ErrorDetailsDTO errorDetailsDTO = new ErrorDetailsDTO(
                HttpStatus.FORBIDDEN.name(),
                e.getMessage(),
                e.getDetails()
        );
        return new ResponseEntity<>(new ErrorResponseDTO(errorDetailsDTO), HttpStatus.FORBIDDEN);
    }
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponseDTO> handleConflictException(ConflictException e) {
        ErrorDetailsDTO details = new ErrorDetailsDTO(
                HttpStatus.CONFLICT.name(),
                e.getMessage(),
                e.getDetails()
        );
        return new ResponseEntity<>(new ErrorResponseDTO(details), HttpStatus.CONFLICT);
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFoundException(ResourceNotFoundException e) {
        ErrorDetailsDTO details = new ErrorDetailsDTO(
                HttpStatus.NOT_FOUND.name(),
                e.getMessage(),
                e.getDetails()
        );
        return new ResponseEntity<>(new ErrorResponseDTO(details), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationException(ValidationException e) {
        ErrorDetailsDTO details = new ErrorDetailsDTO(
                HttpStatus.UNPROCESSABLE_ENTITY.name(),
                e.getMessage(),
                e.getDetails()
        );
        return new ResponseEntity<>(new ErrorResponseDTO(details), HttpStatus.UNPROCESSABLE_ENTITY);
    }
    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<ErrorResponseDTO> handleInternalServerErrorException(ValidationException e) {
        ErrorDetailsDTO details = new ErrorDetailsDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                e.getMessage(),
                e.getDetails()
        );
        return new ResponseEntity<>(new ErrorResponseDTO(details), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
