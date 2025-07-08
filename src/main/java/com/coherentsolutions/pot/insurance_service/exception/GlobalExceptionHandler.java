package com.coherentsolutions.pot.insurance_service.exception;

import com.coherentsolutions.pot.insurance_service.dto.exception.ErrorDetailsDTO;
import com.coherentsolutions.pot.insurance_service.dto.exception.ErrorResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler{
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadRequestException(BadRequestException e) {
        logger.warn("BadRequestException: {}", e.getMessage());
        ErrorDetailsDTO details = new ErrorDetailsDTO(
                HttpStatus.BAD_REQUEST.name(),
                e.getMessage(),
                e.getDetails()
        );
        return new ResponseEntity<>(new ErrorResponseDTO(details), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<ErrorResponseDTO> handleInsufficientAuthenticationException(InsufficientAuthenticationException e) {
        logger.warn("InsufficientAuthenticationException: {}", e.getMessage(), e);
        ErrorDetailsDTO details = new ErrorDetailsDTO(
                HttpStatus.UNAUTHORIZED.name(),
                e.getMessage(),
                e.getDetails()
        );
        return new ResponseEntity<>(new ErrorResponseDTO(details), HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponseDTO> handleForbiddenException(ForbiddenException e) {
        logger.warn("ForbiddenException: {}", e.getMessage(), e);
        ErrorDetailsDTO details = new ErrorDetailsDTO(
                HttpStatus.FORBIDDEN.name(),
                e.getMessage(),
                e.getDetails()
        );
        return new ResponseEntity<>(new ErrorResponseDTO(details), HttpStatus.FORBIDDEN);
    }
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponseDTO> handleConflictException(ConflictException e) {
        logger.warn("ConflictException: {}", e.getMessage(), e);
        ErrorDetailsDTO details = new ErrorDetailsDTO(
                HttpStatus.CONFLICT.name(),
                e.getMessage(),
                e.getDetails()
        );
        return new ResponseEntity<>(new ErrorResponseDTO(details), HttpStatus.CONFLICT);
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFoundException(ResourceNotFoundException e) {
        logger.info("ResourceNotFoundException: {}", e.getMessage());
        ErrorDetailsDTO details = new ErrorDetailsDTO(
                HttpStatus.NOT_FOUND.name(),
                e.getMessage(),
                e.getDetails()
        );
        return new ResponseEntity<>(new ErrorResponseDTO(details), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationException(ValidationException e) {
        logger.warn("ValidationException: {}", e.getMessage(), e);
        ErrorDetailsDTO details = new ErrorDetailsDTO(
                HttpStatus.UNPROCESSABLE_ENTITY.name(),
                e.getMessage(),
                e.getDetails()
        );
        return new ResponseEntity<>(new ErrorResponseDTO(details), HttpStatus.UNPROCESSABLE_ENTITY);
    }
    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<ErrorResponseDTO> handleInternalServerErrorException(InternalServerErrorException e) {
        logger.error("InternalServerErrorException: {}", e.getMessage(), e);
        ErrorDetailsDTO details = new ErrorDetailsDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                e.getMessage(),
                e.getDetails()
        );
        return new ResponseEntity<>(new ErrorResponseDTO(details), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(GenericApiException.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericApiException(GenericApiException e) {
        logger.warn("GenericApiException (code={}): {}", e.getCode(), e.getMessage(), e);
        ErrorDetailsDTO details = new ErrorDetailsDTO(
                e.getCode(),
                e.getMessage(),
                e.getDetails()
        );
        return new ResponseEntity<>(new ErrorResponseDTO(details), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(FieldError::getField, fe-> Objects.requireNonNull(fe.getDefaultMessage(), "null")));
        ErrorDetailsDTO details = new ErrorDetailsDTO(
                HttpStatus.BAD_REQUEST.name(),
                "Validation failed",
                Map.of("fields", fieldErrors)
        );
        return new ResponseEntity<>(new ErrorResponseDTO(details), HttpStatus.BAD_REQUEST);
    }
}
