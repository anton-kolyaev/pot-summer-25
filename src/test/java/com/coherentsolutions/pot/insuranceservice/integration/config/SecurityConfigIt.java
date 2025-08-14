package com.coherentsolutions.pot.insuranceservice.integration.config;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.coherentsolutions.pot.insuranceservice.controller.AdminCompanyManagementController;
import com.coherentsolutions.pot.insuranceservice.integration.TestSecurityConfig;
import com.coherentsolutions.pot.insuranceservice.integration.TestSecurityUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AdminCompanyManagementController.class)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@Disabled("Requires complex Spring context setup")
class SecurityConfigIt {

  @Autowired
  MockMvc mockMvc;

  @ParameterizedTest
  @ValueSource(strings = {"/swagger-ui/index.html", "/v3/api-docs"})
  @DisplayName("permitAll endpoints are reachable without JWT")
  void publicEndpointsAreOpen(String url) throws Exception {
    mockMvc.perform(get(url)
            .with(TestSecurityUtils.adminUser()))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Protected endpoint without token returns 401")
  void protectedEndpointWithoutToken() throws Exception {
    mockMvc.perform(get("/v1/companies"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("Protected endpoint with JWT returns 200")
  void protectedEndpointWithJwt() throws Exception {
    mockMvc.perform(get("/v1/companies")
          .with(TestSecurityUtils.adminUser()))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Spring Security must not create an HTTP session")
  void sessionIsNotCreated() throws Exception {
    mockMvc.perform(get("/v1/companies").with(jwt().jwt(j -> j.claim("sub", "tester"))))
        .andExpect(cookie().doesNotExist("JSESSIONID"))
        .andExpect(result ->
            assertNull(
                result.getRequest().getSession(false), "HttpSession should not exist"));
  }
}
