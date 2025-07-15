package com.coherentsolutions.pot.insurance_service.exception;

import com.coherentsolutions.pot.insurance_service.dto.error.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.time.Instant;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static java.util.Map.entry;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @SafeVarargs
    private static Map<String, Object> buildDetails(HttpServletRequest req, Map.Entry<String, Object>... extras){
        Map<String, Object> details = new HashMap<>();
        details.put("timestamp", Instant.now().toString());
        details.put("endpoint",  req.getMethod() + " " + req.getRequestURI());
        if(extras!=null){
            for (Map.Entry<String, Object> e : extras) {
                if(e!=null){
                    details.put(e.getKey(), e.getValue());
                }
            }
        }
        return details;
    }
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            @Nullable Object body,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode statusCode,
            @NonNull WebRequest request){
        HttpServletRequest servletRequest = ((ServletWebRequest) request).getRequest();
        Map<String,Object> details = buildDetails(servletRequest);
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
        Map<String, Object> details = buildDetails(servletRequest, entry("validationErrors", errorFields));
        ErrorResponseDto error = new ErrorResponseDto(
                ((HttpStatus) statusCode).name(),
                summary,
                details
        );
        return new ResponseEntity<>(error, headers, statusCode);
    }
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode statusCode,
            @NonNull WebRequest request) {
        HttpServletRequest servletRequest = ((ServletWebRequest) request).getRequest();
        String summary = "Malformed JSON request";
        Map<String, Object> details = buildDetails(
                servletRequest,
                entry("cause", ex.getMostSpecificCause().getMessage())
        );
        ErrorResponseDto error = new ErrorResponseDto(
                ((HttpStatus) statusCode).name(),
                summary,
                details
        );
        return new ResponseEntity<>(error, headers, statusCode);
    }
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode statusCode,
            @NonNull WebRequest request) {
        HttpServletRequest servletRequest = ((ServletWebRequest) request).getRequest();
        String summary = "Required request parameter '" + ex.getParameterName() + "' is missing";
        Map<String, Object> details = buildDetails(
                servletRequest,
                entry("parameter", ex.getParameterName())
        );
        ErrorResponseDto error = new ErrorResponseDto(
                ((HttpStatus) statusCode).name(),
                summary,
                details
        );
        return new ResponseEntity<>(error, headers, statusCode);
    }
    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode statusCode,
            @NonNull WebRequest request) {
        HttpServletRequest servletRequest = ((ServletWebRequest) request).getRequest();
        String summary = "Type mismatch for parameter '" + ex.getPropertyName() + "'";
        Map<String, Object> details = buildDetails(
                servletRequest,
                new SimpleImmutableEntry<>("value", ex.getValue()),
                new SimpleImmutableEntry<>(
                        "requiredType",
                        ex.getRequiredType() != null
                                ? ex.getRequiredType().getSimpleName()
                                : null)
        );
        ErrorResponseDto error = new ErrorResponseDto(
                ((HttpStatus) statusCode).name(),
                summary,
                details
        );
        return new ResponseEntity<>(error, headers, statusCode);
    }
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode statusCode,
            @NonNull WebRequest request) {
        HttpServletRequest servletRequest = ((ServletWebRequest) request).getRequest();
        String summary = "Unsupported media type '" + ex.getContentType() + "'";
        Map<String, Object> details = buildDetails(
                servletRequest,
                new SimpleImmutableEntry<>("unsupported", ex.getContentType()),
                new SimpleImmutableEntry<>("supported", ex.getSupportedMediaTypes())
        );
        ErrorResponseDto error = new ErrorResponseDto(
                ((HttpStatus) statusCode).name(),
                summary,
                details
        );
        return new ResponseEntity<>(error, headers, statusCode);
    }
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode statusCode,
            @NonNull WebRequest request) {
        HttpServletRequest servletRequest = ((ServletWebRequest) request).getRequest();
        String summary = "HTTP method '" + ex.getMethod() + "' is not supported for this endpoint";
        Map<String, Object> details = buildDetails(
                servletRequest,
                entry("methodUsed", ex.getMethod()),
                new SimpleImmutableEntry<>("supportedMethods", ex.getSupportedMethods())
        );
        ErrorResponseDto error = new ErrorResponseDto(
                ((HttpStatus) statusCode).name(),
                summary,
                details
        );
        return new ResponseEntity<>(error, headers, statusCode);
    }
    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode statusCode,
            @NonNull WebRequest request) {
        HttpServletRequest servletRequest = ((ServletWebRequest) request).getRequest();
        String summary = "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL();
        Map<String, Object> details = buildDetails(servletRequest);
        ErrorResponseDto error = new ErrorResponseDto(
                ((HttpStatus) statusCode).name(),
                summary,
                details
        );
        return new ResponseEntity<>(error, headers, statusCode);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception ex, HttpServletRequest servletRequest){
        Map<String,Object> details = buildDetails(servletRequest);
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                ex.getMessage(),
                details
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
