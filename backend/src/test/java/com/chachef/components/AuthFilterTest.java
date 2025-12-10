package com.chachef.components;

import com.chachef.dataobjects.AuthContext;
import com.chachef.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthFilterTest {

    @Mock
    JwtService jwtService;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    FilterChain filterChain;

    @InjectMocks
    AuthFilter authFilter;

    @Test
    void doFilter_noAuthHeader_allowsRequestThroughWithoutAuthContext() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        authFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
        verify(response, never()).setStatus(anyInt());
        verify(request, never()).setAttribute(eq("auth"), any());
    }

    @Test
    void doFilter_invalidToken_returns401AndStopsChain() throws ServletException, IOException {
        String token = "badtoken";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        when(jwtService.getUserId(token)).thenThrow(new RuntimeException("invalid token"));

        authFilter.doFilterInternal(request, response, filterChain);

        verify(jwtService).getUserId(token);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(any(), any());
        verify(request, never()).setAttribute(eq("auth"), any());
    }

    @Test
    void doFilter_validToken_setsAuthContextAndContinuesChain() throws ServletException, IOException {
        String token = "goodtoken";
        UUID userId = UUID.randomUUID();
        String username = "alice";
        String name = "Alice Doe";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.getUserId(token)).thenReturn(userId);
        when(jwtService.getUsername(token)).thenReturn(username);
        when(jwtService.getName(token)).thenReturn(name);

        authFilter.doFilterInternal(request, response, filterChain);

        ArgumentCaptor<AuthContext> captor = ArgumentCaptor.forClass(AuthContext.class);
        verify(request).setAttribute(eq("auth"), captor.capture());
        AuthContext ctx = captor.getValue();

        assertEquals(userId, ctx.getUserId());
        assertEquals(username, ctx.getUsername());
        assertEquals(name, ctx.getName());

        verify(filterChain).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
    }
}
