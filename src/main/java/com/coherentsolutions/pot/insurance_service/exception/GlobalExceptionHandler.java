package com.coherentsolutions.pot.insurance_service.exception;

import com.coherentsolutions.pot.insurance_service.dto.error.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            @Nullable Object body,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode statusCode,
            @NonNull WebRequest request){
        HttpServletRequest servletRequest = ((ServletWebRequest) request).getRequest();
        Map<String,Object> details = Map.of(
                "timestamp", Instant.now().toString(),
                "endpoint",  servletRequest.getMethod() + " " + servletRequest.getRequestURI()
        );
        ErrorResponseDto error = new ErrorResponseDto(
                ((HttpStatus) statusCode).name(),
                ex.getMessage(),
                details
        );
        return new ResponseEntity<>(error, headers, statusCode);
    }
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode statusCode,
            @NonNull WebRequest request) {
        HttpServletRequest servletRequest = ((ServletWebRequest) request).getRequest();
        Map<String, List<String>> errorFields = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
                ));
        String summary = "Validation failed for fields: " + String.join(", ", errorFields.keySet());
        Map<String, Object> details = Map.of(
                "timestamp", Instant.now().toString(),
                "endpoint", servletRequest.getMethod() + " " + servletRequest.getRequestURI(),
                "validationErrors", errorFields
        );
        ErrorResponseDto error = new ErrorResponseDto(
                ((HttpStatus) statusCode).name(),
                summary,
                details
        );
        return new ResponseEntity<>(error, headers, statusCode);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception ex, HttpServletRequest request){
        Map<String,Object> details = Map.of(
                "timestamp", Instant.now().toString(),
                "endpoint",  request.getMethod() + " " + request.getRequestURI()
        );
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                ex.getMessage(),
                details
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
