package com.coherentsolutions.pot.insurance_service.unit.exception;

import com.coherentsolutions.pot.insurance_service.dto.error.ErrorResponseDto;
import com.coherentsolutions.pot.insurance_service.exception.GlobalExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTests {
    private final TestableGlobalExceptionHandler handler = new TestableGlobalExceptionHandler();

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
            buildDetailsMethod = GlobalExceptionHandler.class.getDeclaredMethod(
                    "buildDetails",
                    HttpServletRequest.class,
                    Map.Entry[].class
            );
            buildDetailsMethod.setAccessible(true);
        }
        
        @Test
        @DisplayName("should include timestamp, endpoint, and one extra")
        void includesTimestampEndpointAndExtras() throws Exception {
            // Given
            Map.Entry<String, Object> extra = new AbstractMap.SimpleImmutableEntry<>("key", "value");
            
            // When
            @SuppressWarnings("unchecked")
            Map<String, Object> details = (Map<String, Object>) buildDetailsMethod.invoke(
                    null,
                    servletRequest,
                    new Map.Entry[]{extra}
            );
            
            // Then
            assertEquals(3, details.size());
            assertTrue(details.containsKey("timestamp"));
            assertTrue(details.containsKey("endpoint"));
            assertTrue(details.containsKey("key"));
            assertEquals("value", details.get("key"));
            assertEndpointAndTimestamp(details);
        }
        
        @Test
        @DisplayName("should include only timestamp and endpoint when extras is null")
        void onlyTimestampAndEndpoint() throws Exception {
            // Given / When
            @SuppressWarnings("unchecked")
            Map<String, Object> details = (Map<String, Object>) buildDetailsMethod.invoke(
                    null,
                    servletRequest,
                    null
            );
            
            // Then
            assertEquals(2, details.size());
            assertTrue(details.containsKey("timestamp"));
            assertTrue(details.containsKey("endpoint"));
            assertEndpointAndTimestamp(details);
        }
        
        @Test
        @DisplayName("should include timestamp, endpoint, one extra, and skip null extra")
        void skipsNullExtrasEntries() throws Exception {
            // Given
            Map.Entry<String, Object> extra = new AbstractMap.SimpleImmutableEntry<>("key", "value");
            
            // When
            @SuppressWarnings("unchecked")
            Map<String, Object> details = (Map<String, Object>) buildDetailsMethod.invoke(
                    null,
                    servletRequest,
                    new Map.Entry[]{extra, null}
            );
            
            // Then
            assertEquals(3, details.size());
            assertTrue(details.containsKey("timestamp"));
            assertTrue(details.containsKey("endpoint"));
            assertTrue(details.containsKey("key"));
            assertEquals("value", details.get("key"));
            assertEndpointAndTimestamp(details);
        }
        
        @Test
        @DisplayName("should include timestamp, endpoint, and multiple extras entries")
        void includesMultipleExtras() throws Exception {
            // Given
            Map.Entry<String, Object> extra1 = new AbstractMap.SimpleImmutableEntry<>("firstInput", 123);
            Map.Entry<String, Object> extra2 = new AbstractMap.SimpleImmutableEntry<>("secondInput", false);
            
            // When
            @SuppressWarnings("unchecked")
            Map<String, Object> details = (Map<String, Object>) buildDetailsMethod.invoke(
                    null,
                    servletRequest,
                    new Map.Entry[]{extra1, extra2}
            );
            
            // Then
            assertEquals(4, details.size());
            assertTrue(details.containsKey("timestamp"));
            assertTrue(details.containsKey("endpoint"));
            assertTrue(details.containsKey("firstInput"));
            assertTrue(details.containsKey("secondInput"));
            assertEquals(123, details.get("firstInput"));
            assertEquals(false, details.get("secondInput"));
            assertEndpointAndTimestamp(details);
        }
        
        @Test
        @DisplayName("should include timestamp, endpoint, and handle an empty extras array")
        void handlesEmptyExtrasArray() throws Exception {
            // Given / When
            @SuppressWarnings("unchecked")
            Map<String, Object> details = (Map<String, Object>) buildDetailsMethod.invoke(
                    null,
                    servletRequest,
                    new Map.Entry[0]
            );
            
            // Then
            assertEquals(2, details.size());
            assertTrue(details.containsKey("timestamp"));
            assertTrue(details.containsKey("endpoint"));
            assertEndpointAndTimestamp(details);
        }
    }
    
    @Nested
    @DisplayName("handleExceptionInternal tests")
    class HandleExceptionInternalTests {
        
        @Test
        @DisplayName("should build ErrorResponseDto and propagate headers")
        void buildsErrorResponseDto_withHeader() {
            // Given
            IllegalArgumentException exception = new IllegalArgumentException("Test-Exception");
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.add("Test-Header", "testValue");
            
            // When
            ResponseEntity<Object> response = handler.handleExceptionInternal(
                    exception,
                    null,
                    requestHeaders,
                    HttpStatus.BAD_REQUEST,
                    webRequest
            );
            
            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals(requestHeaders, response.getHeaders());
            
            ErrorResponseDto dto = dtoFrom(response);
            assertEquals(HttpStatus.BAD_REQUEST.name(), dto.getCode());
            assertEquals("Test-Exception", dto.getMessage());

            Map<String, Object> details = detailsFrom(response);
            assertEndpointAndTimestamp(details);
            assertTrue(details.containsKey("timestamp"));
        }
        
        @Test
        @DisplayName("should handle exceptions with null message")
        void handlesNullExceptionMessage() {
            // Given
            RuntimeException exception = new RuntimeException((String) null);
            HttpHeaders emptyHeaders = new HttpHeaders();
            
            // When
            ResponseEntity<Object> response = handler.handleExceptionInternal(
                    exception,
                    null,
                    emptyHeaders,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    webRequest);
            
            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

            ErrorResponseDto dto = dtoFrom(response);
            assertEquals("INTERNAL_SERVER_ERROR", dto.getCode());
            assertNull(dto.getMessage());

            Map<String, Object> details = detailsFrom(response);
            assertTrue(details.containsKey("timestamp"));
            assertEndpointAndTimestamp(details);
        }
        
        @Test
        @DisplayName("should propagate all incoming headers unchanged")
        void propagatesAllIncomingHeaders() {
            // Given
            IllegalStateException exception = new IllegalStateException("oops");
            HttpHeaders incomingHeaders = new HttpHeaders();
            incomingHeaders.add("Test-Header-First", "abc123");
            incomingHeaders.add("Test-Header-Second", "___456");
            
            // When
            ResponseEntity<Object> response = handler.handleExceptionInternal(
                    exception,
                    null,
                    incomingHeaders,
                    HttpStatus.CONFLICT,
                    webRequest);
            
            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());

            Map<String, List<String>> headers = response.getHeaders();
            assertEquals(List.of("abc123"), headers.get("Test-Header-First"));
            assertEquals(List.of("___456"), headers.get("Test-Header-Second"));
        }
    }
    
    @Nested
    @DisplayName("handleMethodArgumentNotValid tests")
    class HandleMethodArgumentNotValidTests {

        @SuppressWarnings("unused")
        void TestControllerMethod(String foo) { }

        @Test
        @DisplayName("should return 400 and include all validation error details")
        void shouldReturnBadRequestWithValidationErrorDetails() throws Exception {
            // Given
            BeanPropertyBindingResult bindingResult =
                    new BeanPropertyBindingResult(new Object(), "target");
            bindingResult.addError(new FieldError("target", "name", "must not be null"));
            bindingResult.addError(new FieldError("target", "age",  "must be greater than 0"));
            MethodParameter param = new MethodParameter(
                    getClass().getDeclaredMethod("TestControllerMethod", String.class),
                    0
            );
            MethodArgumentNotValidException ex = new MethodArgumentNotValidException(param, bindingResult);
            HttpHeaders requestHeaders = new HttpHeaders();
            
            // When
            ResponseEntity<Object> response = handler.handleMethodArgumentNotValid(
                    ex,
                    requestHeaders,
                    HttpStatus.BAD_REQUEST,
                    webRequest
            );
            
            // Then
            assertNotNull(response, "response should not be null");
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

            ErrorResponseDto dto = dtoFrom(response);
            assertEquals(HttpStatus.BAD_REQUEST.name(), dto.getCode());
            assertEquals(
                    "Validation failed for fields: name, age",
                    dto.getMessage()
            );
            Map<String,Object> details = detailsFrom(response);
            assertNotNull(details.get("timestamp"));
            assertEndpointAndTimestamp(details);
            
            @SuppressWarnings("unchecked")
            Map<String,List<String>> validationErrors = (Map<String,List<String>>) details.get("validationErrors");
            assertEquals( List.of("must not be null"), validationErrors.get("name") );
            assertEquals( List.of("must be greater than 0"), validationErrors.get("age")  );
        }
    }
    
    @Nested
    @DisplayName("handleHttpMessageNotReadable tests")
    class HandleHttpMessageNotReadableTests {
        private static final String CAUSE = "Bad JSON";
        
        @Test
        @DisplayName("should build ErrorResponseDto with the root cause message")
        void buildsMalformedRequestResponse_withRootCause() {
            // Given
            Throwable rootCause = new java.io.IOException(CAUSE);
            MockHttpInputMessage inputMessage = new MockHttpInputMessage(new byte[0]);
            HttpMessageNotReadableException ex =
                    new HttpMessageNotReadableException("unused‑wrapper", rootCause, inputMessage);
            HttpHeaders requestHeaders = new HttpHeaders();
            
            // When
            ResponseEntity<Object> response =
                    handler.handleHttpMessageNotReadable(
                            ex,
                            requestHeaders,
                            HttpStatus.BAD_REQUEST,
                            webRequest
                    );
            
            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            ErrorResponseDto dto = dtoFrom(response);
            assertEquals(HttpStatus.BAD_REQUEST.name(), dto.getCode());
            assertEquals("Malformed JSON request", dto.getMessage());
            Map<String, Object> details = detailsFrom(response);
            assertEquals(CAUSE, details.get("cause"));
            assertEndpointAndTimestamp(details);
        }
        
        @Test
        @DisplayName("should fall back to exception message when no cause is set")
        void buildsMalformedRequestResponse_whenNoUnderlyingCause() {
            // Given
            MockHttpInputMessage inputMessage = new MockHttpInputMessage(new byte[0]);
            HttpMessageNotReadableException ex =
                    new HttpMessageNotReadableException("parse failed", inputMessage);
            HttpHeaders requestHeaders = new HttpHeaders();
            
            // When
            ResponseEntity<Object> response =
                    handler.handleHttpMessageNotReadable(
                            ex,
                            requestHeaders,
                            HttpStatus.BAD_REQUEST,
                            webRequest
                    );
            
            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            ErrorResponseDto dto = dtoFrom(response);
            assertEquals(HttpStatus.BAD_REQUEST.name(), dto.getCode());
            assertEquals("Malformed JSON request", dto.getMessage());
            Map<String, Object> details = detailsFrom(response);
            assertEquals("parse failed", details.get("cause"));
            assertEndpointAndTimestamp(details);
        }
    }
    
    @Nested
    @DisplayName("handleMissingServletRequestParameter tests")
    class HandleMissingServletRequestParameterTests {
        private static final String PARAM_NAME = "id";
        private static final String PARAM_TYPE = "String";

        @Test
        @DisplayName("should return 400 and include missing-parameter details")
        void shouldReturnBadRequestWithMissingParameterDetails() {
            // Given
            MissingServletRequestParameterException ex =
                    new MissingServletRequestParameterException(PARAM_NAME, PARAM_TYPE);
            HttpHeaders requestHeaders = new HttpHeaders();

            // When
            ResponseEntity<Object> response = handler.handleMissingServletRequestParameter(
                    ex,
                    requestHeaders,
                    HttpStatus.BAD_REQUEST,
                    webRequest
            );

            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals(requestHeaders, response.getHeaders(), "incoming headers must be propagated");
            
            ErrorResponseDto dto = dtoFrom(response);
            assertEquals(HttpStatus.BAD_REQUEST.name(),  dto.getCode());
            assertEquals(
                    "Required request parameter '" + PARAM_NAME + "' is missing",
                    dto.getMessage()
            );
            Map<String, Object> details = detailsFrom(response);
            assertEquals(PARAM_NAME, details.get("parameter"));
            assertEndpointAndTimestamp(details);
        }
    }
    
    @Nested
    @DisplayName("handleTypeMismatch tests")
    class HandleTypeMismatchTests {
        
        @Test
        @DisplayName("should build ErrorResponseDto when parameter type mismatches")
        void buildsTypeMismatchErrorResponse() {
            // Given
            TypeMismatchException ex = new TypeMismatchException("abc", Integer.class);
            ex.initPropertyName("age");
            HttpHeaders requestHeaders = new HttpHeaders();

            // When
            ResponseEntity<Object> response = handler.handleTypeMismatch(
                    ex,
                    requestHeaders,
                    HttpStatus.BAD_REQUEST,
                    webRequest
            );

            // Then
            assertNotNull(response, "response must not be null");
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
                    "status should be" + " " + HttpStatus.BAD_REQUEST);
            assertEquals(requestHeaders, response.getHeaders(), "incoming headers should be propagated");

            ErrorResponseDto dto = dtoFrom(response);
            assertEquals(HttpStatus.BAD_REQUEST.name(), dto.getCode(),
                    "code must match" + " " + HttpStatus.BAD_REQUEST.value());
            assertEquals("Type mismatch for parameter 'age'", dto.getMessage());

            Map<String, Object> details = detailsFrom(response);
            assertEquals("abc", details.get("value"), "should include the raw value");
            assertEquals("Integer", details.get("requiredType"), "should include the required type");
            assertEquals("GET /test-endpoint", details.get("endpoint"), "should include the endpoint");

            assertNotNull(details.get("timestamp"), "timestamp must be present");
        }
    }
    
    @Nested
    @DisplayName("handleHttpMediaTypeNotSupported tests")
    class HandleHttpMediaTypeNotSupportedTests {

        private static final MediaType UNSUPPORTED_TYPE = MediaType.TEXT_PLAIN;
        private static final List<MediaType> SUPPORTED_TYPES =
                List.of(MediaType.APPLICATION_JSON,
                        MediaType.APPLICATION_XML
                );

        @Test
        @DisplayName("should build ErrorResponseDto when media type is unsupported")
        void buildsUnsupportedMediaTypeErrorResponse() {
            // Given
            HttpMediaTypeNotSupportedException ex =
                    new HttpMediaTypeNotSupportedException(UNSUPPORTED_TYPE, SUPPORTED_TYPES);
            HttpHeaders requestHeaders = new HttpHeaders();

            // When
            ResponseEntity<Object> response = handler.handleHttpMediaTypeNotSupported(
                    ex,
                    requestHeaders,
                    HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    webRequest
            );

            // Then
            assertNotNull(response,"response must not be null");
            assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getStatusCode());
            assertEquals(requestHeaders, response.getHeaders(),
                    "incoming headers should be propagated");

            ErrorResponseDto dto = dtoFrom(response);
            assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE.name(), dto.getCode());
            assertEquals(
                    "Unsupported media type '" + UNSUPPORTED_TYPE + "'",
                    dto.getMessage()
            );
            
            @SuppressWarnings("unchecked")
            Map<String, Object> details = (Map<String, Object>) dto.getDetails();
            assertEquals(UNSUPPORTED_TYPE, details.get("unsupported"));
            
            @SuppressWarnings("unchecked")
            List<MediaType> supportedFromDetails =
                    (List<MediaType>) details.get("supported");
            
            assertEquals(SUPPORTED_TYPES, supportedFromDetails);
            assertEndpointAndTimestamp(details);
        }
        
        @Test
        @DisplayName("should handle empty supported‐media list without blowing up")
        void handlesEmptySupportedMediaList() {
            // Given
            HttpMediaTypeNotSupportedException ex =
                    new HttpMediaTypeNotSupportedException(UNSUPPORTED_TYPE, List.of());
            HttpHeaders requestHeaders = new HttpHeaders();

            // When
            ResponseEntity<Object> response = handler.handleHttpMediaTypeNotSupported(
                    ex,
                    requestHeaders,
                    HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    webRequest
            );

            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getStatusCode());
            @SuppressWarnings("unchecked")
            Map<String,Object> details = (Map<String,Object>) dtoFrom(response).getDetails();
            
            assertTrue(((List<?>) details.get("supported")).isEmpty());
            assertEquals(UNSUPPORTED_TYPE, details.get("unsupported"));
            assertEndpointAndTimestamp(details);
        }
    }
    
    @Nested
    @DisplayName("handleHttpRequestMethodNotSupported tests")
    class HandleHttpRequestMethodNotSupportedTests {
        private static final String UNSUPPORTED_METHOD = "DELETE";
        private static final List<String> ALLOWED_METHODS = List.of("GET", "POST");

        @Test
        @DisplayName("should return METHOD_NOT_ALLOWED and include supportedMethods array")
        void shouldReturnMethodNotAllowedWithSupportedMethodsDetail() {
            // Given
            HttpRequestMethodNotSupportedException ex =
                    new HttpRequestMethodNotSupportedException(UNSUPPORTED_METHOD, ALLOWED_METHODS);
            HttpHeaders requestHeaders = new HttpHeaders();

            // When
            ResponseEntity<Object> response = handler.handleHttpRequestMethodNotSupported(
                    ex,
                    requestHeaders,
                    HttpStatus.METHOD_NOT_ALLOWED,
                    webRequest
            );

            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
            assertEquals(requestHeaders, response.getHeaders());

            ErrorResponseDto dto = dtoFrom(response);
            assertEquals(HttpStatus.METHOD_NOT_ALLOWED.name(), dto.getCode());
            String expectedMessage =
                    "HTTP method '" + UNSUPPORTED_METHOD + "' is not supported for this endpoint";
            assertEquals(expectedMessage, dto.getMessage());
            
            @SuppressWarnings("unchecked")
            Map<String,Object> details = (Map<String,Object>) dto.getDetails();
            assertEquals(UNSUPPORTED_METHOD, details.get("methodUsed"));
            
            Object rawSupported = details.get("supportedMethods");
            assertInstanceOf(String[].class, rawSupported);
            String[] supportedArray = (String[]) rawSupported;
            assertArrayEquals(
                    ALLOWED_METHODS.toArray(new String[0]),
                    supportedArray
            );
            assertEndpointAndTimestamp(details);
        }
        
        @Test
        @DisplayName("when no allowed methods provided, supportedMethods is null")
        void handlesNullSupportedMethods() {
            // Given
            HttpRequestMethodNotSupportedException ex =
                    new HttpRequestMethodNotSupportedException(UNSUPPORTED_METHOD);
            HttpHeaders requestHeaders = new HttpHeaders();

            // When
            ResponseEntity<Object> response = handler.handleHttpRequestMethodNotSupported(
                    ex,
                    requestHeaders,
                    HttpStatus.METHOD_NOT_ALLOWED,
                    webRequest
            );

            // Then
            assertNotNull(response);
            @SuppressWarnings("unchecked")
            Map<String,Object> details =
                    (Map<String,Object>) dtoFrom(response).getDetails();
            assertTrue(details.containsKey("supportedMethods"));
            assertNull(details.get("supportedMethods"));
        }
    }
    
    @Nested
    @DisplayName("handleNoHandlerFoundException tests")
    class HandleNoHandlerFoundExceptionTests {
        
        @Test
        @DisplayName("should return NOT_FOUND with correct error details")
        void shouldReturnNotFoundWithCorrectErrorDetails() {
            // Given
            NoHandlerFoundException ex =
                    new NoHandlerFoundException("PATCH", "/missing", new HttpHeaders());
            HttpHeaders requestHeaders = new HttpHeaders();

            // When
            ResponseEntity<Object> response = handler.handleNoHandlerFoundException(
                    ex,
                    requestHeaders,
                    HttpStatus.NOT_FOUND,
                    webRequest
            );

            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertEquals(requestHeaders, response.getHeaders());

            ErrorResponseDto dto = dtoFrom(response);
            assertEquals(HttpStatus.NOT_FOUND.name(), dto.getCode());
            assertEquals("No handler found for PATCH /missing",
                    dto.getMessage()
            );

            Map<String, Object> details = detailsFrom(response);
            assertEndpointAndTimestamp(details);
        }

        @Test
        @DisplayName("should handle unexpected method or URL values")
        void shouldIncludeWhateverSpringProvides() {
            // Given
            NoHandlerFoundException ex =
                    new NoHandlerFoundException("OPTIONS", "/something-else", new HttpHeaders());
            HttpHeaders requestHeaders = new HttpHeaders();

            // When
            ResponseEntity<Object> response = handler.handleNoHandlerFoundException(
                    ex, requestHeaders, HttpStatus.NOT_FOUND, webRequest);

            // Then
            assertNotNull(response);
            ErrorResponseDto dto = dtoFrom(response);
            String expected = "No handler found for OPTIONS /something-else";
            assertEquals(expected, dto.getMessage());
        }
    }

    @Nested
    @DisplayName("handleGenericException tests")
    class HandleGenericExceptionTests {
        private static final String EXCEPTION_MESSAGE = "Something went wrong";

        @Test
        @DisplayName("should return 500 and include exception message and endpoint")
        void shouldReturnInternalErrorWithMessage() {
            // Given
            RuntimeException exception = new RuntimeException(EXCEPTION_MESSAGE);

            // When
            ResponseEntity<ErrorResponseDto> response =
                    handler.handleGenericException(exception, servletRequest);

            // Then
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            ErrorResponseDto dto = dtoFrom(response);
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.name(), dto.getCode());
            assertEquals(EXCEPTION_MESSAGE, dto.getMessage());

            Map<String, Object> details = detailsFrom(response);
            assertEndpointAndTimestamp(details);
        }

        @Test
        @DisplayName("should return 500 and handle null exception message")
        void shouldHandleNullExceptionMessage() {
            // Given
            RuntimeException exception = new RuntimeException((String) null);

            // When
            ResponseEntity<ErrorResponseDto> response =
                    handler.handleGenericException(exception, servletRequest);

            // Then
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            ErrorResponseDto dto = dtoFrom(response);
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.name(), dto.getCode());
            assertNull(dto.getMessage());

            Map<String, Object> details = detailsFrom(response);
            assertEndpointAndTimestamp(details);
        }
    }
    private static ErrorResponseDto dtoFrom(ResponseEntity<?> response) {
        Object body = response.getBody();
        assertInstanceOf(ErrorResponseDto.class, body);
        return (ErrorResponseDto) body;
    }
    private void assertEndpointAndTimestamp(Map<String, Object> details) {
        assertEquals("GET /test-endpoint", details.get("endpoint"), "should include the endpoint");
        assertNotNull(details.get("timestamp"), "should include a timestamp");
    }
    @SuppressWarnings("unchecked")
    private static Map<String, Object> detailsFrom(ResponseEntity<?> response) {
        return (Map<String, Object>) dtoFrom(response).getDetails();
    }
}
