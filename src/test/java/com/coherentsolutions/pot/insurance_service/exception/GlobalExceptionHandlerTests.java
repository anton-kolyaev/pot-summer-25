package com.coherentsolutions.pot.insurance_service.exception;

import com.coherentsolutions.pot.insurance_service.dto.error.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTests {
    private static final String DEFAULT_URI = "/test-endpoint";
    private static final String DEFAULT_METHOD = "GET";
    private static final String DEFAULT_ENDPOINT = DEFAULT_METHOD + " " + DEFAULT_URI;
    private static final HttpStatus DEFAULT_STATUS = HttpStatus.BAD_REQUEST;
    
    @InjectMocks
    private GlobalExceptionHandler handler;

    @Mock
    private HttpServletRequest servletRequest;

    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        when(servletRequest.getMethod()).thenReturn(DEFAULT_METHOD);
        when(servletRequest.getRequestURI()).thenReturn(DEFAULT_URI);
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
            assertEquals(DEFAULT_ENDPOINT, details.get("endpoint"));
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
            assertEquals(DEFAULT_ENDPOINT, details.get("endpoint"));
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
            assertEquals(DEFAULT_ENDPOINT, details.get("endpoint"));
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
            assertEquals(DEFAULT_ENDPOINT, details.get("endpoint"));
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
            assertEquals(DEFAULT_ENDPOINT, details.get("endpoint"));
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
                    DEFAULT_STATUS,
                    webRequest
            );
            // Then
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getHeaders()).isEqualTo(requestHeaders);

            ErrorResponseDto dto = dtoFrom(response);
            assertThat(dto.getCode()).isEqualTo(DEFAULT_STATUS.name());
            assertThat(dto.getMessage()).isEqualTo("Test-Exception");

            Map<String, Object> details = detailsFrom(response);
            assertThat(details)
                    .containsEntry("endpoint", DEFAULT_ENDPOINT)
                    .containsKey("timestamp");
        }
        @Test
        @DisplayName("should handle exceptions with null message")
        void handlesNullExceptionMessage() {
            // Given
            RuntimeException exception = new RuntimeException((String) null);
            HttpHeaders emptyHeaders = new HttpHeaders();
            HttpStatusCode responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            // When
            ResponseEntity<Object> response = handler.handleExceptionInternal(
                    exception,
                    null,
                    emptyHeaders,
                    responseStatus,
                    webRequest);
            // Then
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

            ErrorResponseDto dto = dtoFrom(response);
            assertThat(dto.getCode()).isEqualTo("INTERNAL_SERVER_ERROR");
            assertThat(dto.getMessage()).isNull();

            Map<String, Object> details = detailsFrom(response);
            assertThat(details)
                    .containsEntry("endpoint", DEFAULT_ENDPOINT)
                    .containsKey("timestamp");
        }
        @Test
        @DisplayName("should propagate all incoming headers unchanged")
        void propagatesAllIncomingHeaders() {
            // Given
            IllegalStateException exception = new IllegalStateException("oops");
            HttpHeaders incomingHeaders = new HttpHeaders();
            incomingHeaders.add("Test-Header-First", "abc123");
            incomingHeaders.add("Test-Header-Second", "___456");
            HttpStatusCode responseStatus = HttpStatus.CONFLICT;
            
            // When
            ResponseEntity<Object> response = handler.handleExceptionInternal(
                    exception,
                    null,
                    incomingHeaders,
                    responseStatus,
                    webRequest);
            
            // Then
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getHeaders())
                    .containsEntry("Test-Header-First", List.of("abc123"))
                    .containsEntry("Test-Header-Second", List.of("___456"));
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
            MethodArgumentNotValidException ex =
                    new MethodArgumentNotValidException(param, bindingResult);
            HttpHeaders requestHeaders = new HttpHeaders();
            
            // When
            ResponseEntity<Object> response = handler.handleMethodArgumentNotValid(
                    ex,
                    requestHeaders,
                    DEFAULT_STATUS,
                    webRequest
            );
            
            // Then
            assertNotNull(response, "response should not be null");
            assertEquals(DEFAULT_STATUS, response.getStatusCode());

            ErrorResponseDto dto = dtoFrom(response);
            assertEquals(DEFAULT_STATUS.name(), dto.getCode());
            assertEquals(
                    "Validation failed for fields: name, age",
                    dto.getMessage()
            );
            Map<String,Object> details = detailsFrom(response);
            assertEquals(DEFAULT_ENDPOINT, details.get("endpoint"));
            assertNotNull(details.get("timestamp"));

            @SuppressWarnings("unchecked")
            Map<String,List<String>> validationErrors =
                    (Map<String,List<String>>) details.get("validationErrors");
            assertEquals( List.of("must not be null"),
                    validationErrors.get("name") );
            assertEquals( List.of("must be greater than 0"),
                    validationErrors.get("age")  );
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
                            DEFAULT_STATUS,
                            webRequest
                    );
            
            // Then
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(DEFAULT_STATUS);
            ErrorResponseDto dto = dtoFrom(response);
            assertThat(dto.getCode()).isEqualTo(DEFAULT_STATUS.name());
            assertThat(dto.getMessage()).isEqualTo("Malformed JSON request");
            Map<String, Object> details = detailsFrom(response);
            assertThat(details).containsEntry("cause", CAUSE);
            assertThat(details).containsEntry("endpoint", DEFAULT_ENDPOINT);
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
                            DEFAULT_STATUS,
                            webRequest
                    );
            
            // Then
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(DEFAULT_STATUS);
            ErrorResponseDto dto = dtoFrom(response);
            assertThat(dto.getCode()).isEqualTo(DEFAULT_STATUS.name());
            assertThat(dto.getMessage()).isEqualTo("Malformed JSON request");
            Map<String, Object> details = detailsFrom(response);
            assertThat(details).containsEntry("cause", "parse failed");
            assertThat(details).containsEntry("endpoint", DEFAULT_ENDPOINT);
        }
    }
    @Nested
    @DisplayName("handleMissingServletRequestParameter tests")
    class HandleMissingServletRequestParameterTests {
        private static final String PARAM_NAME = "id";
        private static final String PARAM_TYPE = "String";
        private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

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
                    STATUS,
                    webRequest
            );

            // Then
            assertNotNull(response);
            assertEquals(STATUS, response.getStatusCode());
            assertEquals(requestHeaders, response.getHeaders(), "incoming headers must be propagated");
            
            ErrorResponseDto dto = dtoFrom(response);
            assertEquals(STATUS.name(),  dto.getCode());
            assertEquals(
                    "Required request parameter '" + PARAM_NAME + "' is missing",
                    dto.getMessage()
            );
            
            Map<String, Object> details = detailsFrom(response);
            assertEquals(PARAM_NAME, details.get("parameter"));
            assertEquals(DEFAULT_ENDPOINT, details.get("endpoint"));
            assertNotNull(details.get("timestamp"));
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
                    DEFAULT_STATUS,
                    webRequest
            );

            // Then
            assertNotNull(response, "response must not be null");
            assertEquals(DEFAULT_STATUS, response.getStatusCode(), "status should be" + " " + DEFAULT_STATUS);
            assertEquals(requestHeaders, response.getHeaders(), "incoming headers should be propagated");

            ErrorResponseDto dto = dtoFrom(response);
            assertEquals(DEFAULT_STATUS.name(), dto.getCode(), "code must match" + " " + DEFAULT_STATUS.value());
            assertEquals("Type mismatch for parameter 'age'", dto.getMessage());

            Map<String, Object> details = detailsFrom(response);
            assertEquals("abc", details.get("value"), "should include the raw value");
            assertEquals("Integer", details.get("requiredType"), "should include the required type");
            assertEquals(DEFAULT_ENDPOINT, details.get("endpoint"), "should include the endpoint");

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
        private static final HttpStatus DEFAULT_STATUS = HttpStatus.UNSUPPORTED_MEDIA_TYPE;

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
                    DEFAULT_STATUS,
                    webRequest
            );

            // Then
            assertNotNull(response,"response must not be null");
            assertEquals(DEFAULT_STATUS, response.getStatusCode());
            assertEquals(requestHeaders, response.getHeaders(),
                    "incoming headers should be propagated");

            ErrorResponseDto dto = dtoFrom(response);
            assertEquals(DEFAULT_STATUS.name(), dto.getCode());
            assertEquals(
                    "Unsupported media type '" + UNSUPPORTED_TYPE.toString() + "'",
                    dto.getMessage()
            );
            @SuppressWarnings("unchecked")
            Map<String, Object> details = (Map<String, Object>) dto.getDetails();
            assertEquals(UNSUPPORTED_TYPE, details.get("unsupported"));
            @SuppressWarnings("unchecked")
            List<MediaType> supportedFromDetails =
                    (List<MediaType>) details.get("supported");
            assertEquals(SUPPORTED_TYPES, supportedFromDetails);
            assertEquals(DEFAULT_ENDPOINT, details.get("endpoint"));
            assertNotNull(details.get("timestamp"));
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
                    DEFAULT_STATUS,
                    webRequest
            );

            // Then
            assertNotNull(response);
            assertEquals(DEFAULT_STATUS, response.getStatusCode());
            @SuppressWarnings("unchecked")
            Map<String,Object> details = (Map<String,Object>) dtoFrom(response).getDetails();
            assertTrue(((List<?>) details.get("supported")).isEmpty());
            assertEquals(UNSUPPORTED_TYPE, details.get("unsupported"));
            assertEquals(DEFAULT_ENDPOINT, details.get("endpoint"));
        }

    }

    @Nested
    @DisplayName("handleHttpRequestMethodNotSupported tests")
    class HandleHttpRequestMethodNotSupportedTests {
        @Test
        @DisplayName("should build ErrorResponseDto when HTTP method is not supported")
        void buildsMethodNotSupportedErrorResponse() {
            HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException(
                    "DELETE", java.util.List.of("GET","POST"));
            HttpHeaders headers = new HttpHeaders();
            HttpStatusCode status = HttpStatus.METHOD_NOT_ALLOWED;
            ResponseEntity<Object> response = handler.handleHttpRequestMethodNotSupported(
                    ex, headers, status, webRequest);
            assertNotNull(response);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
            assertThat(response.getHeaders()).isEqualTo(headers);
            ErrorResponseDto dto = dtoFrom(response);
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
        @DisplayName("should build ErrorResponseDto when no handler is found")
        void buildsNoHandlerFoundErrorResponse() {
            NoHandlerFoundException ex = new NoHandlerFoundException("PATCH", "/missing", new HttpHeaders());
            HttpHeaders headers = new HttpHeaders();
            HttpStatusCode status = HttpStatus.NOT_FOUND;
            ResponseEntity<Object> response = handler.handleNoHandlerFoundException(
                    ex, headers, status, webRequest);
            assertNotNull(response);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getHeaders()).isEqualTo(headers);
            ErrorResponseDto dto = dtoFrom(response);
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
        @DisplayName("should build INTERNAL_SERVER_ERROR ErrorResponseDto for generic exception")
        void buildsGenericErrorResponse() {
            RuntimeException ex = new RuntimeException("Something went wrong");
            ResponseEntity<ErrorResponseDto> response = handler.handleGenericException(ex, servletRequest);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            ErrorResponseDto dto = response.getBody();
            assertThat(dto).isNotNull();
            assertThat(dto.getCode()).isEqualTo("INTERNAL_SERVER_ERROR");
            assertThat(dto.getMessage()).isEqualTo("Something went wrong");
            Map<String,Object> details = detailsFrom(response);
            assertThat(details).containsEntry("endpoint", "GET /test-endpoint");
            assertThat(details).containsKey("timestamp");
        }
    }
    private static ErrorResponseDto dtoFrom(ResponseEntity<?> response) {
        Object body = response.getBody();
        assertThat(body)
                .as("Response body should be an ErrorResponseDto")
                .isInstanceOf(ErrorResponseDto.class);
        return (ErrorResponseDto) body;
    }
    @SuppressWarnings("unchecked")
    private static Map<String, Object> detailsFrom(ResponseEntity<?> response) {
        return (Map<String, Object>) dtoFrom(response).getDetails();
    }
    @SuppressWarnings("unchecked")
    private static Map<String, List<String>> validationErrorsFrom(ResponseEntity<?> response) {
        Object errors = detailsFrom(response).get("validationErrors");
        return (Map<String, List<String>>) errors;
    }
    
}
