package com.chachef.service;

import com.chachef.dataobjects.ApplicationJwtBuilder;
import com.chachef.entity.RefreshTokens;
import com.chachef.repository.RefreshTokensRepository;
import com.chachef.repository.UserRepository;
import com.chachef.service.exceptions.InvalidUserException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final Key accessSecret;
    private final Key refreshSecret;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokensRepository refreshTokensRepository;

    public JwtService(@Value("${ACCESS_SECRET}") String accessSecret,  @Value("${REFRESH_SECRET}") String refreshSecret) {
        this.accessSecret = Keys.hmacShaKeyFor(accessSecret.getBytes());
        this.refreshSecret = Keys.hmacShaKeyFor(refreshSecret.getBytes());
    }

    public String generateAccessToken(UUID userId, String username, String name) {
        return new ApplicationJwtBuilder().setExpire(600).setType("access").setSecret(accessSecret).setUserId(userId).setUsername(username).setName(name).build();
    }

    public String generateRefreshToken(UUID userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new InvalidUserException(userId.toString());
        }
        String jwt = new ApplicationJwtBuilder().setExpire(604800).setType("refresh").setSecret(refreshSecret).setUserId(userId).build();
        RefreshTokens refreshTokens = new RefreshTokens(UUID.fromString(getRefreshJti(jwt)), getClaimsFromToken(jwt, refreshSecret).getExpiration().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), userRepository.findById(userId).get());
        refreshTokensRepository.save(refreshTokens);
        return jwt;
    }

    public boolean isValidAccessToken(String token) {
        return validateToken(token, accessSecret);
    }

    public boolean isValidRefreshToken(String token) {
        return validateToken(token, refreshSecret);
    }

    private boolean validateToken(String token, Key key) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public UUID getUserId(String token) {
        return UUID.fromString(getClaimsFromToken(token, accessSecret).getSubject());
    }

    public String getUsername(String token) {
        return getClaimsFromToken(token, accessSecret).get("username", String.class);
    }

    public String getName(String token) {
        return getClaimsFromToken(token, accessSecret).get("name", String.class);
    }

    public UUID getRefreshUserId(String token) {
        return UUID.fromString(getClaimsFromToken(token, refreshSecret).getSubject());
    }

    public String getRefreshJti(String token) {
        return getClaimsFromToken(token, refreshSecret).getId();
    }

    private Claims getClaimsFromToken(String token, Key key) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}
