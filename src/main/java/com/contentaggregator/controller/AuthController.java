package com.contentaggregator.controller;

import com.contentaggregator.dto.AuthRequest;
import com.contentaggregator.exception.InvalidCodeException;
import com.contentaggregator.response.AuthResponse;
import com.contentaggregator.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest, HttpServletResponse response) {
        String code = authRequest.getCode();
        if (code == null || code.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Authorization code is required.");
        }

        try {
            AuthResponse authResponse = authService.exchangeCodeForToken(code);
            // Setting HttpOnly cookie for token
            Cookie cookie = new Cookie("access_token", authResponse.getAccessToken());
            cookie.setHttpOnly(true);
            cookie.setSecure(true); // Ensure the cookie is sent only over HTTPS
            cookie.setPath("/");
            response.addCookie(cookie);
            return ResponseEntity.ok().body("Logged in successfully.");
        } catch (InvalidCodeException ice) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ice.getMessage());
        } catch (AuthenticationException ae) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error.");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = new Cookie("access_token", null); // Nullify the cookie
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // Match security settings
        cookie.setPath("/");
        cookie.setMaxAge(0); // Expire the cookie immediately
        response.addCookie(cookie);
        return ResponseEntity.ok("Logged out successfully.");
    }

    @PostMapping("/exchange")
    public ResponseEntity<?> exchangeCodeForTokens(@RequestBody Map<String, String> payload) {
        String code = payload.get("code");
        try {
            AuthResponse authResponse = authService.exchangeCodeForToken(code);
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to exchange code for tokens: " + e.getMessage());
        }
    }
}
