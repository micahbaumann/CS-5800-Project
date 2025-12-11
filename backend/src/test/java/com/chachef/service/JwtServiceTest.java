package com.chachef.service;

import com.chachef.entity.User;
import com.chachef.repository.RefreshTokensRepository;
import com.chachef.repository.UserRepository;
import com.chachef.service.exceptions.InvalidUserException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokensRepository refreshTokensRepository;

    private JwtService jwtService;

    private final String accessSecretString = "testaccesssecrettestaccesssecrettestaccesssecret";
    private final String refreshSecretString = "testrefreshsecrettestrefreshsecrettestrefreshsecret";
    private final Key accessSecret = Keys.hmacShaKeyFor(accessSecretString.getBytes());
    private final Key refreshSecret = Keys.hmacShaKeyFor(refreshSecretString.getBytes());


    @BeforeEach
    void setUp() {
        jwtService = new JwtService(accessSecretString, refreshSecretString);
        ReflectionTestUtils.setField(jwtService, "userRepository", userRepository);
        ReflectionTestUtils.setField(jwtService, "refreshTokensRepository", refreshTokensRepository);
    }

    private Claims getClaimsFromToken(String token, Key key) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    @Test
    void generateAccessToken() {
        UUID userId = UUID.randomUUID();
        String username = "testuser";
        String name = "Test User";

        String token = jwtService.generateAccessToken(userId, username, name);

        assertNotNull(token);
        Claims claims = getClaimsFromToken(token, accessSecret);
        assertEquals(userId.toString(), claims.getSubject());
        assertEquals(username, claims.get("username", String.class));
        assertEquals(name, claims.get("name", String.class));
    }

    @Test
    void generateRefreshToken() {
        UUID userId = UUID.randomUUID();
        User user = new User("test", "test", "test");
        user.setUserId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        String token = jwtService.generateRefreshToken(userId);

        assertNotNull(token);
        verify(refreshTokensRepository).save(any());
        Claims claims = getClaimsFromToken(token, refreshSecret);
        assertEquals(userId.toString(), claims.getSubject());
    }

    @Test
    void generateRefreshToken_invalidUser() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(InvalidUserException.class, () -> jwtService.generateRefreshToken(userId));

        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(refreshTokensRepository);
    }


    @Test
    void isValidAccessToken() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateAccessToken(userId, "user", "name");
        assertTrue(jwtService.isValidAccessToken(token));
    }

    @Test
    void isValidAccessToken_invalid() {
        assertFalse(jwtService.isValidAccessToken("invalid.token.string"));
    }

    @Test
    void isValidRefreshToken() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setUserId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        String token = jwtService.generateRefreshToken(userId);
        assertTrue(jwtService.isValidRefreshToken(token));
    }

    @Test
    void isValidRefreshToken_invalid() {
        assertFalse(jwtService.isValidRefreshToken("invalid.token.string"));
    }

    @Test
    void getUserId() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateAccessToken(userId, "user", "name");
        assertEquals(userId, jwtService.getUserId(token));
    }

    @Test
    void getUsername() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateAccessToken(userId, "user", "name");
        assertEquals("user", jwtService.getUsername(token));
    }

    @Test
    void getName() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateAccessToken(userId, "user", "name");
        assertEquals("name", jwtService.getName(token));
    }

    @Test
    void getRefreshUserId() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setUserId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        String token = jwtService.generateRefreshToken(userId);
        assertEquals(userId, jwtService.getRefreshUserId(token));
    }

    @Test
    void getRefreshJti() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setUserId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        String token = jwtService.generateRefreshToken(userId);
        assertNotNull(jwtService.getRefreshJti(token));
    }
}