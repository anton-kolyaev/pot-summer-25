package com.coherentsolutions.pot.insurance_service.unit.exception;

import com.coherentsolutions.pot.insurance_service.exception.GlobalExceptionHandler;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

public class TestableGlobalExceptionHandler extends GlobalExceptionHandler {

    @Override
    public ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            Object body,
            @NotNull HttpHeaders headers,
            @NotNull HttpStatusCode statusCode,
            @NotNull WebRequest request
    ) {
        return super.handleExceptionInternal(ex, body, headers, statusCode, request);
    }
    
    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NotNull HttpHeaders headers,
            @NotNull HttpStatusCode statusCode,
            @NotNull WebRequest request
    ) {
        return super.handleMethodArgumentNotValid(ex, headers, statusCode, request);
    }

    @Override
    public ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            @NotNull HttpHeaders headers,
            @NotNull HttpStatusCode statusCode,
            @NotNull WebRequest request
    ) {
        return super.handleHttpMessageNotReadable(ex, headers, statusCode, request);
    }

    @Override
    public ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            @NotNull HttpHeaders headers,
            @NotNull HttpStatusCode statusCode,
            @NotNull WebRequest request
    ) {
        return super.handleMissingServletRequestParameter(ex, headers, statusCode, request);
    }

    @Override
    public ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException ex,
            @NotNull HttpHeaders headers,
            @NotNull HttpStatusCode statusCode,
            @NotNull WebRequest request
    ) {
        return super.handleTypeMismatch(ex, headers, statusCode, request);
    }

    @Override
    public ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            @NotNull HttpHeaders headers,
            @NotNull HttpStatusCode statusCode,
            @NotNull WebRequest request
    ) {
        return super.handleHttpMediaTypeNotSupported(ex, headers, statusCode, request);
    }

    @Override
    public ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            @NotNull HttpHeaders headers,
            @NotNull HttpStatusCode statusCode,
            @NotNull WebRequest request
    ) {
        return super.handleHttpRequestMethodNotSupported(ex, headers, statusCode, request);
    }

    @Override
    public ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex,
            @NotNull HttpHeaders headers,
            @NotNull HttpStatusCode statusCode,
            @NotNull WebRequest request
    ) {
        return super.handleNoHandlerFoundException(ex, headers, statusCode, request);
    }
}
