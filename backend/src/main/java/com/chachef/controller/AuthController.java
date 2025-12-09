package com.chachef.controller;

import com.chachef.annotations.RequireAuth;
import com.chachef.dataobjects.AuthContext;
import com.chachef.dto.LoginDto;
import com.chachef.entity.Booking;
import com.chachef.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginDto loginDto, HttpServletResponse response) {
        Map<String, String> returnRequest = authService.login(loginDto.getUsername(), loginDto.getPassword());

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", returnRequest.get("refresh"))
            .httpOnly(true)
            .secure(false)
            .path("/")
            .maxAge(7 * 24 * 60 * 60)
            .sameSite("Lax")
            .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return new ResponseEntity<>(Map.of("access", returnRequest.get("access")), HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(name = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {
        if (refreshToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        ResponseCookie clearCookie = ResponseCookie.from("refreshToken", "")
            .httpOnly(true)
            .secure(false)
            .path("/")
            .maxAge(0)
            .sameSite("Lax")
            .build();

        response.addHeader(HttpHeaders.SET_COOKIE, clearCookie.toString());

        authService.logout(refreshToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequireAuth
    @PostMapping("/logout/all")
    public ResponseEntity<Void> logoutAll(@RequestAttribute(value = "auth") AuthContext authContext, @CookieValue(name = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {

        if (refreshToken != null) {
            ResponseCookie clearCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

            response.addHeader(HttpHeaders.SET_COOKIE, clearCookie.toString());
        }

        authService.logoutAll(authContext);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@CookieValue(name = "refreshToken", required = false) String refreshToken, HttpServletResponse response){
        if (refreshToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        Map<String, String> returnRequest = authService.refresh(refreshToken);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", returnRequest.get("refresh"))
            .httpOnly(true)
            .secure(false)
            .path("/")
            .maxAge(7 * 24 * 60 * 60)
            .sameSite("Lax")
            .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return new ResponseEntity<>(Map.of("access", returnRequest.get("access")), HttpStatus.OK);
    }
}
