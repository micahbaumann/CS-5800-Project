package com.chachef.controller;

import com.chachef.dataobjects.AuthContext;
import com.chachef.service.AuthService;
import com.chachef.service.JwtService;
import io.jsonwebtoken.Jwt;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Use @MockBean for components that are dependencies (like AuthService)
    // to be injected into the Controller being tested.
    @MockitoBean
    private AuthService mockAuthService;

    @MockitoBean
    private JwtService mockJwtService;

    private final String username = "username";
    private final String password = "password";
    private final String accessToken = "mock.access.token";
    private final String refreshToken = "mock.refresh.token";
    private final UUID uuid = UUID.randomUUID();

    @BeforeEach
    public void setUp() {
        when(mockJwtService.getUsername(any(String.class))).thenReturn(username);
        when(mockJwtService.getUserId(any(String.class))).thenReturn(uuid);
        when(mockJwtService.getName(any(String.class))).thenReturn(username);
    }

    // Utility for creating the JSON payload
    private String createLoginPayload() {
        return """
            {
              "username": "%s",
              "password": "%s"
            }
            """.formatted(username, password);
    }

    // --- TEST /auth/login ---
    @Test
    @DisplayName("TEST /auth/login - Successful login")
    void login_Success() throws Exception {
        // ARRANGE: Set up the mock service to return tokens
        when(mockAuthService.login(anyString(), anyString()))
                .thenReturn(Map.of("access", accessToken, "refresh", refreshToken));

        String payload = createLoginPayload();

        // ACT & ASSERT: Perform the POST request and check results
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk()) // 200 OK
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.access").value(accessToken))
                .andExpect(cookie().exists("refreshToken"))
                .andExpect(header().string("Set-Cookie", containsString("refreshToken=%s".formatted(refreshToken))));

        // ASSERT: Verify AuthService was called with the correct credentials
        ArgumentCaptor<String> usernameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> passwordCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockAuthService, times(1)).login(usernameCaptor.capture(), passwordCaptor.capture());

        assertEquals(username, usernameCaptor.getValue());
        assertEquals(password, passwordCaptor.getValue());
    }

    @Test
    @DisplayName("TEST /auth/login - Invalid input (missing field)")
    void login_InvalidInput() throws Exception {
        String invalidPayload = """
            {
              "username": "%s"
              // password missing! @Valid will catch this.
            }
            """.formatted(username);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest()); // 400 Bad Request (due to @Valid/missing field)

        // ASSERT: Verify AuthService was NOT called
        verify(mockAuthService, never()).login(anyString(), anyString());
    }

    // --- TEST /auth/logout ---
    @Test
    @DisplayName("TEST /auth/logout - With refreshToken cookie")
    void logout_WithCookie() throws Exception {
        // ARRANGE: Define the cookie value
        String initialRefreshToken = "existing.refresh.token";
        Cookie refreshCookie = new Cookie("refreshToken", initialRefreshToken);

        // ACT & ASSERT: Perform POST and check for successful response and cookie clearing
        mockMvc.perform(post("/auth/logout")
                        .cookie(refreshCookie))
                .andExpect(status().isNoContent()) // 204 No Content
                .andExpect(cookie().exists("refreshToken"))
                .andExpect(header().string("Set-Cookie", containsString("Max-Age=0"))); // Verifies cookie is cleared

        // ASSERT: Verify AuthService.logout was called with the correct token
        verify(mockAuthService, times(1)).logout(initialRefreshToken);
    }

    @Test
    @DisplayName("TEST /auth/logout - Missing refreshToken cookie")
    void logout_MissingCookie() throws Exception {
        // ACT & ASSERT: Perform POST without the cookie
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isUnauthorized()); // 401 Unauthorized (per logic in controller)

        // ASSERT: Verify AuthService.logout was NOT called
        verify(mockAuthService, never()).logout(anyString());
    }

    // --- TEST /auth/logout/all ---
    @Test
    @DisplayName("TEST /auth/logout/all - Successful logout all with cookie")
    void logoutAll_WithCookie() throws Exception {
        // ARRANGE: Set up the mock AuthContext (provided by the @RequireAuth interceptor/filter)
        AuthContext mockAuthContext = new AuthContext(uuid, "user@example.com", "USER");
        String initialRefreshToken = "existing.refresh.token";

        // To simulate the interceptor setting the @RequestAttribute, we use .requestAttr()
        mockMvc.perform(post("/auth/logout/all")
                        .cookie(new Cookie("refreshToken", initialRefreshToken))
                        .requestAttr("auth", mockAuthContext)) // Simulates @RequireAuth putting AuthContext in request
                .andExpect(status().isNoContent()) // 204 No Content
                .andExpect(cookie().exists("refreshToken"))
                .andExpect(header().string("Set-Cookie", containsString("Max-Age=0"))); // Cookie clearing

        // ASSERT: Verify AuthService.logoutAll was called with the correct AuthContext
        ArgumentCaptor<AuthContext> authContextCaptor = ArgumentCaptor.forClass(AuthContext.class);
        verify(mockAuthService, times(1)).logoutAll(authContextCaptor.capture());
        assertEquals(mockAuthContext.getUserId(), authContextCaptor.getValue().getUserId());
    }

    @Test
    @DisplayName("TEST /auth/logout/all - Successful logout all without cookie")
    void logoutAll_WithoutCookie() throws Exception {
        // ARRANGE: Set up the mock AuthContext
        AuthContext mockAuthContext = new AuthContext(uuid, "user@example.com", "USER");

        mockMvc.perform(post("/auth/logout/all")
                        .requestAttr("auth", mockAuthContext)) // Simulates @RequireAuth
                .andExpect(status().isNoContent()) // 204 No Content
                .andExpect(header().doesNotExist("Set-Cookie")); // No Set-Cookie header expected

        // ASSERT: Verify AuthService.logoutAll was called
        verify(mockAuthService, times(1)).logoutAll(mockAuthContext);
    }

    // NOTE: Testing the failure of @RequireAuth (e.g., missing AuthContext) is usually done in the Filter/Interceptor test, not the Controller test.

    // --- TEST /auth/refresh ---
    @Test
    @DisplayName("TEST /auth/refresh - Successful token refresh")
    void refresh_Success() throws Exception {
        // ARRANGE: Set up mock service to return new tokens
        String initialRefreshToken = "old.refresh.token";
        String newAccessToken = "new.access.token";
        String newRefreshToken = "new.refresh.token";

        when(mockAuthService.refresh(initialRefreshToken))
                .thenReturn(Map.of("access", newAccessToken, "refresh", newRefreshToken));

        // ACT & ASSERT: Perform POST with the cookie
        mockMvc.perform(post("/auth/refresh")
                        .cookie(new Cookie("refreshToken", initialRefreshToken)))
                .andExpect(status().isOk()) // 200 OK
                .andExpect(jsonPath("$.access").value(newAccessToken)) // Check response body
                .andExpect(cookie().exists("refreshToken"))
                .andExpect(header().string("Set-Cookie", containsString("refreshToken=%s".formatted(newRefreshToken)))); // Check new cookie set

        // ASSERT: Verify AuthService.refresh was called with the correct token
        verify(mockAuthService, times(1)).refresh(initialRefreshToken);
    }

    @Test
    @DisplayName("TEST /auth/refresh - Missing refreshToken cookie")
    void refresh_MissingCookie() throws Exception {
        // ACT & ASSERT: Perform POST without the cookie
        mockMvc.perform(post("/auth/refresh"))
                .andExpect(status().isUnauthorized()); // 401 Unauthorized (per controller logic)

        // ASSERT: Verify AuthService.refresh was NOT called
        verify(mockAuthService, never()).refresh(anyString());
    }

    @Test
    @DisplayName("TEST /auth/refresh - AuthService failure (e.g., invalid token)")
    void refresh_AuthServiceFailure() throws Exception {
        // ARRANGE: Simulate an exception from the service (e.g., if the token is invalid)
        String initialRefreshToken = "bad.refresh.token";
        // The controller should typically handle service exceptions (or the service throws the ResponseStatusException directly)
        // For simplicity, we'll assume the service throws an exception that results in a 401.
        doThrow(new ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED))
                .when(mockAuthService).refresh(initialRefreshToken);

        // ACT & ASSERT: Perform POST
        mockMvc.perform(post("/auth/refresh")
                        .cookie(new Cookie("refreshToken", initialRefreshToken)))
                .andExpect(status().isUnauthorized()); // 401 Unauthorized
    }
}