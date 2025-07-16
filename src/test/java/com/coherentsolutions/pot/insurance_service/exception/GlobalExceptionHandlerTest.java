package com.coherentsolutions.pot.insurance_service.exception;

import com.coherentsolutions.pot.insurance_service.dto.error.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler handler;

    @Mock
    private HttpServletRequest servletRequest;

    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        when(servletRequest.getMethod()).thenReturn("GET");
        when(servletRequest.getRequestURI()).thenReturn("/test-endpoint");
        webRequest = new ServletWebRequest(servletRequest);
    }
    @Nested
    @DisplayName("buildDetails tests")
    class BuildDetailsTests {

        private Method buildDetailsMethod;

        @BeforeEach
        void initialBuildDetailsMethod() throws NoSuchMethodException {
            buildDetailsMethod = GlobalExceptionHandler.class
                    .getDeclaredMethod("buildDetails", HttpServletRequest.class, Map.Entry[].class);
            buildDetailsMethod.setAccessible(true);
        }
        @Test
        @DisplayName("Should include timestamp, endpoint, and extras when provided")
        void includesTimestampEndpointAndExtras() throws Exception {
            AbstractMap.SimpleImmutableEntry<String, Object> extra =
                    new AbstractMap.SimpleImmutableEntry<>("key", "value");
            @SuppressWarnings("unchecked")
            Map<String, Object> details = (Map<String, Object>) buildDetailsMethod.invoke(
                    null,
                    servletRequest,
                    new Map.Entry[]{extra}
            );
            assertThat(details).containsKeys("timestamp", "endpoint", "key");
            assertThat(details.get("key")).isEqualTo("value");
            assertThat(details.get("endpoint")).isEqualTo("GET /test-endpoint");
        }
        @Test
        @DisplayName("Should include only timestamp and endpoint when extras is null")
        void onlyTimestampAndEndpoint() throws Exception {
            @SuppressWarnings("unchecked")
            Map<String, Object> details = (Map<String, Object>) buildDetailsMethod.invoke(
                    null,
                    servletRequest,
                    null
            );
            assertThat(details).containsKeys("timestamp", "endpoint");
            assertThat(details).hasSize(2);
            assertThat(details.get("endpoint")).isEqualTo("GET /test-endpoint");
        }
        @Test
        @DisplayName("Should skip null extras entries")
        void includesTimestampEndpointAndNullExtraEntry() throws Exception {
            AbstractMap.SimpleImmutableEntry<String, Object> extra =
                    new AbstractMap.SimpleImmutableEntry<>("key", "value");
            @SuppressWarnings("unchecked")
            Map<String, Object> details = (Map<String, Object>) buildDetailsMethod.invoke(
                    null,
                    servletRequest,
                    new Map.Entry[]{extra, null}
            );
            assertThat(details).containsKeys("timestamp", "endpoint", "key");
            assertThat(details).hasSize(3);
            assertThat(details.get("key")).isEqualTo("value");
            assertThat(details.get("endpoint")).isEqualTo("GET /test-endpoint");
        }
        @Test
        @DisplayName("Should include multiple extras entries correctly")
        void includesTimestampEndpointAndMultipleExtras() throws Exception {
            AbstractMap.SimpleImmutableEntry<String, Object> extra1 =
                    new AbstractMap.SimpleImmutableEntry<>("firstInput", 123);
            AbstractMap.SimpleImmutableEntry<String, Object> extra2 =
                    new AbstractMap.SimpleImmutableEntry<>("secondInput", false);
            @SuppressWarnings("unchecked")
            Map<String, Object> details = (Map<String, Object>) buildDetailsMethod.invoke(
                    null,
                    servletRequest,
                    new Map.Entry[]{extra1, extra2}
            );
            assertThat(details).containsKeys("timestamp", "endpoint", "firstInput", "secondInput");
            assertThat(details.get("firstInput")).isEqualTo(123);
            assertThat(details.get("secondInput")).isEqualTo(false);
            assertThat(details.get("endpoint")).isEqualTo("GET /test-endpoint");
        }
    }
    @Nested
    @DisplayName("handleExceptionInternal tests")
    class HandleExceptionInternalTests {
        @Test
        @DisplayName("Should build ErrorResponseDto for handleExceptionInternal")
        void buildsErrorResponseDto() {
            IllegalArgumentException ex = new IllegalArgumentException("Ups");
            HttpHeaders headers = new HttpHeaders();
            HttpStatusCode status = HttpStatus.BAD_REQUEST;
            ResponseEntity<Object> response = handler.handleExceptionInternal(
                    ex,
                    null,
                    headers,
                    status,
                    webRequest
            );
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            Object body = response.getBody();
            assertThat(body).isInstanceOf(ErrorResponseDto.class);
            ErrorResponseDto dto = (ErrorResponseDto) body;
            assertThat(dto.getCode()).isEqualTo("BAD_REQUEST");
            assertThat(dto.getMessage()).isEqualTo("Ups");

            @SuppressWarnings("unchecked")
            Map<String, Object> details = (Map<String, Object>) dto.getDetails();
            
            assertThat(details).containsEntry("endpoint", "GET /test-endpoint");
            assertThat(details).containsKey("timestamp");
        }
    }
    @Nested
    @DisplayName("handleMethodArgumentNotValid tests")
    class HandleMethodArgumentNotValidTests {
        @Test
        @DisplayName("Should build ErrorResponseDto with validation errors and details")
        void buildsValidationErrorResponse() throws Exception {
            org.springframework.validation.BeanPropertyBindingResult bindingResult =
                    new org.springframework.validation.BeanPropertyBindingResult(new Object(), "target");
            bindingResult.addError(new org.springframework.validation.FieldError(
                    "target", "name", "must not be null"));
            bindingResult.addError(new org.springframework.validation.FieldError(
                    "target", "age", "must be greater than 0"));
            MethodParameter param = new MethodParameter(
                    getClass().getDeclaredMethod("forTest", String.class), 0
            );
            MethodArgumentNotValidException ex = new MethodArgumentNotValidException(param, bindingResult);
            HttpHeaders headers = new HttpHeaders();
            HttpStatusCode status = HttpStatus.BAD_REQUEST;
            ResponseEntity<Object> response = handler.handleMethodArgumentNotValid(
                    ex, headers, status, webRequest);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            @SuppressWarnings("unchecked")
            Map<String, List<String>> errors = (Map<String,List<String>>) ((Map<String,Object>) ((ErrorResponseDto)response.getBody()).getDetails())
                    .get("validationErrors");
            
            assertThat(errors).containsKeys("name","age");
        }
        void forTest(String param) {}
    }
    @Nested
    @DisplayName("handleHttpMessageNotReadable tests")
    class HandleHttpMessageNotReadableTests {
        @Test
        @DisplayName("Should build ErrorResponseDto with cause and details")
        void buildsMalformedRequestResponse() {
            Throwable cause = new java.io.IOException("Bad JSON");
            HttpMessageNotReadableException ex = new HttpMessageNotReadableException("malformed", cause);
            HttpHeaders headers = new HttpHeaders();
            HttpStatusCode status = HttpStatus.BAD_REQUEST;
            ResponseEntity<Object> response = handler.handleHttpMessageNotReadable(
                    ex, headers, status, webRequest
            );
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            @SuppressWarnings("unchecked")
            Map<String,Object> details = (Map<String,Object>) ((ErrorResponseDto)response.getBody()).getDetails();
            
            assertThat(details).containsEntry("cause", "Bad JSON");
        }
    }
    @Nested
    @DisplayName("handleMissingServletRequestParameter tests")
    class HandleMissingServletRequestParameterTests {
        @Test
        @DisplayName("Should build ErrorResponseDto when a required param is missing")
        void buildsMissingParamErrorResponse() {
            org.springframework.web.bind.MissingServletRequestParameterException ex =
                    new org.springframework.web.bind.MissingServletRequestParameterException("id", "String");
            HttpHeaders headers = new HttpHeaders();
            HttpStatusCode status = HttpStatus.BAD_REQUEST;
            ResponseEntity<Object> response = handler.handleMissingServletRequestParameter(
                    ex, headers, status, webRequest);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getHeaders()).isEqualTo(headers);
            ErrorResponseDto dto = (ErrorResponseDto) response.getBody();
            assertThat(dto.getCode()).isEqualTo("BAD_REQUEST");
            assertThat(dto.getMessage()).isEqualTo("Required request parameter 'id' is missing");

            @SuppressWarnings("unchecked")
            Map<String, Object> details = (Map<String, Object>) dto.getDetails();
            
            assertThat(details).containsEntry("parameter", "id");
            assertThat(details).containsEntry("endpoint", "GET /test-endpoint");
        }
    }
    @Nested
    @DisplayName("handleTypeMismatch tests")
    class HandleTypeMismatchTests {
        @Test
        @DisplayName("Should build ErrorResponseDto when parameter type mismatches")
        void buildsTypeMismatchErrorResponse() {
            java.beans.PropertyChangeEvent pce =
                    new java.beans.PropertyChangeEvent(servletRequest, "age", null, "abc");
            org.springframework.beans.TypeMismatchException ex =
                    new org.springframework.beans.TypeMismatchException(pce, Integer.class);
            HttpHeaders headers = new HttpHeaders();
            HttpStatusCode status = HttpStatus.BAD_REQUEST;
            ResponseEntity<Object> response = handler.handleTypeMismatch(
                    ex, headers, status, webRequest);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getHeaders()).isEqualTo(headers);
            ErrorResponseDto dto = (ErrorResponseDto) response.getBody();
            assertThat(dto.getCode()).isEqualTo("BAD_REQUEST");
            assertThat(dto.getMessage()).isEqualTo("Type mismatch for parameter 'age'");

            @SuppressWarnings("unchecked")
            Map<String, Object> details = (Map<String, Object>) dto.getDetails();

            assertThat(details).containsEntry("value", "abc");
            assertThat(details).containsEntry("requiredType", "Integer");
            assertThat(details).containsEntry("endpoint", "GET /test-endpoint");
        }
    }
    @Nested
    @DisplayName("handleHttpMediaTypeNotSupported tests")
    class HandleHttpMediaTypeNotSupportedTests {
        @Test
        @DisplayName("Should build ErrorResponseDto when media type is unsupported")
        void buildsUnsupportedMediaTypeErrorResponse() {
            HttpMediaTypeNotSupportedException ex = new HttpMediaTypeNotSupportedException(
                    MediaType.TEXT_PLAIN, java.util.List.of(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML));
            HttpHeaders headers = new HttpHeaders();
            HttpStatusCode status = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
            ResponseEntity<Object> response = handler.handleHttpMediaTypeNotSupported(
                    ex, headers, status, webRequest);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
            assertThat(response.getHeaders()).isEqualTo(headers);
            ErrorResponseDto dto = (ErrorResponseDto) response.getBody();
            assertThat(dto.getCode()).isEqualTo("UNSUPPORTED_MEDIA_TYPE");
            assertThat(dto.getMessage()).isEqualTo("Unsupported media type 'text/plain'" );

            @SuppressWarnings("unchecked")
            Map<String,Object> details = (Map<String,Object>) dto.getDetails();
            
            assertThat(details).containsEntry("unsupported", MediaType.TEXT_PLAIN);
            Object supportedObj = details.get("supported");
            assertThat(supportedObj).isInstanceOf(java.util.List.class);
            
            @SuppressWarnings("unchecked")
            java.util.List<MediaType> supported = (java.util.List<MediaType>) supportedObj;
            
            assertThat(supported).contains(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML);
            assertThat(details).containsEntry("endpoint", "GET /test-endpoint");
        }
    }
    @Nested
    @DisplayName("handleHttpRequestMethodNotSupported tests")
    class HandleHttpRequestMethodNotSupportedTests {
        @Test
        @DisplayName("Should build ErrorResponseDto when HTTP method is not supported")
        void buildsMethodNotSupportedErrorResponse() {
            HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException(
                    "DELETE", java.util.List.of("GET","POST"));
            HttpHeaders headers = new HttpHeaders();
            HttpStatusCode status = HttpStatus.METHOD_NOT_ALLOWED;
            ResponseEntity<Object> response = handler.handleHttpRequestMethodNotSupported(
                    ex, headers, status, webRequest);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
            assertThat(response.getHeaders()).isEqualTo(headers);
            ErrorResponseDto dto = (ErrorResponseDto) response.getBody();
            assertThat(dto.getCode()).isEqualTo("METHOD_NOT_ALLOWED");
            assertThat(dto.getMessage()).isEqualTo("HTTP method 'DELETE' is not supported for this endpoint");

            @SuppressWarnings("unchecked")
            Map<String,Object> details = (Map<String,Object>) dto.getDetails();
            
            assertThat(details).containsEntry("methodUsed", "DELETE");
            Object supportedObj = details.get("supportedMethods");
            assertThat(supportedObj).isInstanceOf(String[].class);
            String[] supportedMethods = (String[]) supportedObj;
            assertThat(supportedMethods).contains("GET","POST");
            assertThat(details).containsEntry("endpoint", "GET /test-endpoint");
        }
    }
    @Nested
    @DisplayName("handleNoHandlerFoundException tests")
    class HandleNoHandlerFoundExceptionTests {
        @Test
        @DisplayName("Should build ErrorResponseDto when no handler is found")
        void buildsNoHandlerFoundErrorResponse() {
            NoHandlerFoundException ex = new NoHandlerFoundException("PATCH", "/missing", new HttpHeaders());
            HttpHeaders headers = new HttpHeaders();
            HttpStatusCode status = HttpStatus.NOT_FOUND;
            ResponseEntity<Object> response = handler.handleNoHandlerFoundException(
                    ex, headers, status, webRequest);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getHeaders()).isEqualTo(headers);
            ErrorResponseDto dto = (ErrorResponseDto) response.getBody();
            assertThat(dto.getCode()).isEqualTo("NOT_FOUND");
            assertThat(dto.getMessage()).isEqualTo("No handler found for PATCH /missing");

            @SuppressWarnings("unchecked")
            Map<String,Object> details = (Map<String,Object>) dto.getDetails();
            
            assertThat(details).containsEntry("endpoint", "GET /test-endpoint");
            assertThat(details).containsKey("timestamp");
        }
    }
    @Nested
    @DisplayName("handleGenericException tests")
    class HandleGenericExceptionTests {
        @Test
        @DisplayName("Should build INTERNAL_SERVER_ERROR ErrorResponseDto for generic exception")
        void buildsGenericErrorResponse() {
            RuntimeException ex = new RuntimeException("Something exploded");
            ResponseEntity<ErrorResponseDto> response = handler.handleGenericException(ex, servletRequest);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            ErrorResponseDto dto = response.getBody();
            assertThat(dto).isNotNull();
            assertThat(dto.getCode()).isEqualTo("INTERNAL_SERVER_ERROR");
            assertThat(dto.getMessage()).isEqualTo("Something exploded");

            @SuppressWarnings("unchecked")
            Map<String,Object> details = (Map<String,Object>) dto.getDetails();
            
            assertThat(details).containsEntry("endpoint", "GET /test-endpoint");
            assertThat(details).containsKey("timestamp");
        }
    }
    
}
