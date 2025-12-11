package com.chachef.service;

import com.chachef.dataobjects.AuthContext;
import com.chachef.entity.User;
import com.chachef.repository.RefreshTokensRepository;
import com.chachef.repository.UserRepository;
import com.chachef.service.exceptions.InvalidUserException;
import com.chachef.service.exceptions.UnauthorizedUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokensRepository refreshTokensRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    void login() {
        User user = new User("testuser", "Test User", BCrypt.hashpw("password123", BCrypt.gensalt()));
        user.setUserId(UUID.randomUUID());

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(user.getUserId(), user.getUsername(), user.getName())).thenReturn("access_token");
        when(jwtService.generateRefreshToken(user.getUserId())).thenReturn("refresh_token");

        Map<String, String> tokens = authService.login("testuser", "password123");

        assertNotNull(tokens);
        assertEquals("access_token", tokens.get("access"));
        assertEquals("refresh_token", tokens.get("refresh"));

        verify(userRepository).findByUsername("testuser");
        verify(jwtService).generateAccessToken(user.getUserId(), user.getUsername(), user.getName());
        verify(jwtService).generateRefreshToken(user.getUserId());
        verifyNoMoreInteractions(userRepository, jwtService);
    }

    @Test
    void login_invalidUsername() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(InvalidUserException.class, () -> authService.login("nonexistent", "password"));

        verify(userRepository).findByUsername("nonexistent");
        verifyNoMoreInteractions(userRepository, jwtService);
    }

    @Test
    void login_incorrectPassword() {
        User user = new User("testuser", "Test User", BCrypt.hashpw("password123", BCrypt.gensalt()));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        assertThrows(UnauthorizedUser.class, () -> authService.login("testuser", "wrongpassword"));

        verify(userRepository).findByUsername("testuser");
        verifyNoMoreInteractions(userRepository, jwtService);
    }

    @Test
    void logout() {
        String refreshToken = "some_refresh_token";
        String jti = UUID.randomUUID().toString();
        when(jwtService.getRefreshJti(refreshToken)).thenReturn(jti);

        authService.logout(refreshToken);

        verify(refreshTokensRepository).deleteRefreshTokensByRefreshId(UUID.fromString(jti));
        verifyNoMoreInteractions(refreshTokensRepository);
    }

    @Test
    void logoutAll() {
        AuthContext authContext = new AuthContext(UUID.randomUUID(), "test", "test");

        authService.logoutAll(authContext);

        verify(refreshTokensRepository).deleteRefreshTokensByUser_UserId(authContext.getUserId());
        verifyNoMoreInteractions(refreshTokensRepository);
    }

    @Test
    void refresh() {
        String refreshToken = "valid_refresh_token";
        UUID userId = UUID.randomUUID();
        UUID jti = UUID.randomUUID();
        User user = new User("testuser", "Test User", "hashedpassword");
        user.setUserId(userId);

        when(jwtService.isValidRefreshToken(refreshToken)).thenReturn(true);
        when(jwtService.getRefreshJti(refreshToken)).thenReturn(jti.toString());
        when(refreshTokensRepository.findRefreshTokensByRefreshId(jti)).thenReturn(Optional.of(new com.chachef.entity.RefreshTokens()));
        when(jwtService.getRefreshUserId(refreshToken)).thenReturn(userId);
        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(userId, user.getUsername(), user.getName())).thenReturn("new_access_token");
        when(jwtService.generateRefreshToken(userId)).thenReturn("new_refresh_token");

        Map<String, String> tokens = authService.refresh(refreshToken);

        assertNotNull(tokens);
        assertEquals("new_access_token", tokens.get("access"));
        assertEquals("new_refresh_token", tokens.get("refresh"));

        verify(refreshTokensRepository).deleteByExpiresBefore(any(LocalDateTime.class));
        verify(refreshTokensRepository).findRefreshTokensByRefreshId(jti);
        verify(refreshTokensRepository).deleteRefreshTokensByRefreshId(jti);
        verify(userRepository).findByUserId(userId);
        verify(jwtService).generateAccessToken(userId, user.getUsername(), user.getName());
        verify(jwtService).generateRefreshToken(userId);
    }

    @Test
    void refresh_invalidToken() {
        String refreshToken = "invalid_refresh_token";
        when(jwtService.isValidRefreshToken(refreshToken)).thenReturn(false);

        assertThrows(UnauthorizedUser.class, () -> authService.refresh(refreshToken));

        verify(refreshTokensRepository).deleteByExpiresBefore(any(LocalDateTime.class));
        verify(jwtService).isValidRefreshToken(refreshToken);
        verifyNoMoreInteractions(refreshTokensRepository, jwtService, userRepository);
    }

    @Test
    void refresh_userNotFound() {
        String refreshToken = "valid_refresh_token";
        UUID userId = UUID.randomUUID();
        UUID jti = UUID.randomUUID();

        when(jwtService.isValidRefreshToken(refreshToken)).thenReturn(true);
        when(jwtService.getRefreshJti(refreshToken)).thenReturn(jti.toString());
        when(refreshTokensRepository.findRefreshTokensByRefreshId(jti)).thenReturn(Optional.of(new com.chachef.entity.RefreshTokens()));
        when(jwtService.getRefreshUserId(refreshToken)).thenReturn(userId);
        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(InvalidUserException.class, () -> authService.refresh(refreshToken));

        verify(refreshTokensRepository).deleteByExpiresBefore(any(LocalDateTime.class));
        verify(jwtService).isValidRefreshToken(refreshToken);
        verify(jwtService).getRefreshJti(refreshToken);
        verify(refreshTokensRepository).findRefreshTokensByRefreshId(jti);
        verify(jwtService).getRefreshUserId(refreshToken);
        verify(userRepository).findByUserId(userId);
        verifyNoMoreInteractions(refreshTokensRepository, jwtService, userRepository);
    }
}