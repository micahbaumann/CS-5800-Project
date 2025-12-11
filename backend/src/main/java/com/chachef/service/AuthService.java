package com.chachef.service;

import com.chachef.dataobjects.ApplicationJwtBuilder;
import com.chachef.dataobjects.AuthContext;
import com.chachef.entity.User;
import com.chachef.repository.RefreshTokensRepository;
import com.chachef.repository.UserRepository;
import com.chachef.service.exceptions.InvalidUserException;
import com.chachef.service.exceptions.UnauthorizedUser;
import com.chachef.service.exceptions.UsernameTakenException;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    RefreshTokensRepository refreshTokensRepository;

    public Map<String, String> login(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new InvalidUserException(username);
        }

        if (!BCrypt.checkpw(password, user.get().getPasswordHash())) {
            throw new UnauthorizedUser(username);
        }

        Map<String, String> map = new HashMap<>();
        map.put("access", jwtService.generateAccessToken(user.get().getUserId(), user.get().getUsername(), user.get().getName()));
        map.put("refresh", jwtService.generateRefreshToken(user.get().getUserId()));
        return map;
    }

    public void logout(String refreshToken) {
        try {
            refreshTokensRepository.deleteRefreshTokensByRefreshId(UUID.fromString(jwtService.getRefreshJti(refreshToken)));
        } catch (Exception e) {}
    }

    public void logoutAll(AuthContext authContext) {
        try {
            refreshTokensRepository.deleteRefreshTokensByUser_UserId(authContext.getUserId());
        } catch (Exception e) {}
    }

    public Map<String, String> refresh(String refreshToken) {
        refreshTokensRepository.deleteByExpiresBefore(LocalDateTime.now());
        if (!jwtService.isValidRefreshToken(refreshToken)
                || refreshTokensRepository.findRefreshTokensByRefreshId(UUID.fromString(jwtService.getRefreshJti(refreshToken))).isEmpty()) {
            throw new UnauthorizedUser(refreshToken);
        }

        UUID userId = jwtService.getRefreshUserId(refreshToken);
        Optional<User> user = userRepository.findByUserId(userId);
        if (user.isEmpty()) {
            throw new InvalidUserException(userId.toString());
        }

        refreshTokensRepository.deleteRefreshTokensByRefreshId(UUID.fromString(jwtService.getRefreshJti(refreshToken)));

        Map<String, String> map = new HashMap<>();
        map.put("access", jwtService.generateAccessToken(user.get().getUserId(), user.get().getUsername(), user.get().getName()));
        map.put("refresh", jwtService.generateRefreshToken(user.get().getUserId()));
        return map;
    }
}