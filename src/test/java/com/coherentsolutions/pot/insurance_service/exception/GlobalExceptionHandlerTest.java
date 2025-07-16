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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.AbstractMap;
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
        void withExtras_includesTimestampEndpointAndExtras() throws Exception {
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
        void withNullExtras_onlyTimestampAndEndpoint() throws Exception {
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
        void withNullEntryInExtras_skipsNullExtra() throws Exception {
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
        void withMultipleExtras_includesAllEntries() throws Exception {
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
}
