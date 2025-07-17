package com.coherentsolutions.pot.insurance_service.exception;

import com.coherentsolutions.pot.insurance_service.dto.error.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
            HttpStatusCode responseStatus = HttpStatus.BAD_REQUEST;
            // When
            ResponseEntity<Object> response = handler.handleExceptionInternal(
                    exception,
                    null,
                    requestHeaders,
                    responseStatus,
                    webRequest
            );
            // Then
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getHeaders()).isEqualTo(requestHeaders);

            ErrorResponseDto dto = dtoFrom(response);
            assertThat(dto.getCode()).isEqualTo("BAD_REQUEST");
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
            HttpStatusCode responseStatus = HttpStatus.BAD_REQUEST;
            // When
            ResponseEntity<Object> response = handler.handleMethodArgumentNotValid(
                    ex,
                    requestHeaders,
                    responseStatus,
                    webRequest
            );
            // Then
            assertNotNull(response, "response should not be null");
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

            ErrorResponseDto dto = dtoFrom(response);
            assertEquals("BAD_REQUEST", dto.getCode());
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
        @Test
        @DisplayName("should build ErrorResponseDto with cause and details")
        void buildsMalformedRequestResponse() {
            Throwable cause = new java.io.IOException("Bad JSON");
            MockHttpInputMessage inputMessage = new MockHttpInputMessage(new byte[0]);
            HttpMessageNotReadableException ex =
                    new HttpMessageNotReadableException("malformed", cause, inputMessage);
            HttpHeaders headers = new HttpHeaders();
            HttpStatusCode status = HttpStatus.BAD_REQUEST;
            ResponseEntity<Object> response = handler.handleHttpMessageNotReadable(
                    ex,
                    headers,
                    status,
                    webRequest
            );
            assertNotNull(response);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            ErrorResponseDto dto = dtoFrom(response);
            assertThat(dto.getCode()).isEqualTo("BAD_REQUEST");
            assertThat(dto.getMessage()).isEqualTo("Malformed JSON request");
            Map<String, Object> details = detailsFrom(response);
            assertThat(details).containsEntry("cause", "Bad JSON");
            assertThat(details).containsEntry("endpoint", "GET /test-endpoint");
        }

    }
    @Nested
    @DisplayName("handleMissingServletRequestParameter tests")
    class HandleMissingServletRequestParameterTests {
        @Test
        @DisplayName("should build ErrorResponseDto when a required param is missing")
        void buildsMissingParamErrorResponse() {
            org.springframework.web.bind.MissingServletRequestParameterException ex =
                    new org.springframework.web.bind.MissingServletRequestParameterException("id", "String");
            HttpHeaders headers = new HttpHeaders();
            HttpStatusCode status = HttpStatus.BAD_REQUEST;
            ResponseEntity<Object> response = handler.handleMissingServletRequestParameter(
                    ex, headers, status, webRequest);
            assertNotNull(response);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getHeaders()).isEqualTo(headers);
            ErrorResponseDto dto = dtoFrom(response);
            assertThat(dto.getCode()).isEqualTo("BAD_REQUEST");
            assertThat(dto.getMessage()).isEqualTo("Required request parameter 'id' is missing");
            Map<String, Object> details = detailsFrom(response);
            assertThat(details).containsEntry("parameter", "id");
            assertThat(details).containsEntry("endpoint", "GET /test-endpoint");
        }
    }
    @Nested
    @DisplayName("handleTypeMismatch tests")
    class HandleTypeMismatchTests {
        @Test
        @DisplayName("should build ErrorResponseDto when parameter type mismatches")
        void buildsTypeMismatchErrorResponse() {
            java.beans.PropertyChangeEvent pce =
                    new java.beans.PropertyChangeEvent(servletRequest, "age", null, "abc");
            org.springframework.beans.TypeMismatchException ex =
                    new org.springframework.beans.TypeMismatchException(pce, Integer.class);
            HttpHeaders headers = new HttpHeaders();
            HttpStatusCode status = HttpStatus.BAD_REQUEST;
            ResponseEntity<Object> response = handler.handleTypeMismatch(
                    ex, headers, status, webRequest);
            assertNotNull(response);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getHeaders()).isEqualTo(headers);
            ErrorResponseDto dto = dtoFrom(response);
            assertThat(dto.getCode()).isEqualTo("BAD_REQUEST");
            assertThat(dto.getMessage()).isEqualTo("Type mismatch for parameter 'age'");
            Map<String, Object> details = detailsFrom(response);
            assertThat(details).containsEntry("value", "abc");
            assertThat(details).containsEntry("requiredType", "Integer");
            assertThat(details).containsEntry("endpoint", "GET /test-endpoint");
        }
    }
    @Nested
    @DisplayName("handleHttpMediaTypeNotSupported tests")
    class HandleHttpMediaTypeNotSupportedTests {
        @Test
        @DisplayName("should build ErrorResponseDto when media type is unsupported")
        void buildsUnsupportedMediaTypeErrorResponse() {
            HttpMediaTypeNotSupportedException ex = new HttpMediaTypeNotSupportedException(
                    MediaType.TEXT_PLAIN, List.of(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML));
            HttpHeaders headers = new HttpHeaders();
            HttpStatusCode status = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
            ResponseEntity<Object> response = handler.handleHttpMediaTypeNotSupported(
                    ex, headers, status, webRequest);
            assertNotNull(response);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
            assertThat(response.getHeaders()).isEqualTo(headers);
            ErrorResponseDto dto = dtoFrom(response);
            assertThat(dto.getCode()).isEqualTo("UNSUPPORTED_MEDIA_TYPE");
            assertThat(dto.getMessage()).isEqualTo("Unsupported media type 'text/plain'");
            Map<String,Object> details = detailsFrom(response);
            assertThat(details).containsEntry("unsupported", MediaType.TEXT_PLAIN);
            Object supportedObj = details.get("supported");
            assertThat(supportedObj).isInstanceOf(List.class);
            
            @SuppressWarnings("unchecked")
            List<MediaType> supported = (List<MediaType>) supportedObj;
            
            assertThat(supported).contains(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML);
            assertThat(details).containsEntry("endpoint", "GET /test-endpoint");
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
