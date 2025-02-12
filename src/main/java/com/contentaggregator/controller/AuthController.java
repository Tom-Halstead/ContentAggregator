package com.contentaggregator.controller;

import com.contentaggregator.config.CognitoConfig;
import com.contentaggregator.dto.AuthRequest;
import com.contentaggregator.exception.InvalidCodeException;
import com.contentaggregator.response.AuthResponse;
import com.contentaggregator.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.naming.AuthenticationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;


    // Autowire for secured info.
    @Autowired
    private CognitoConfig cognitoConfig;

    @GetMapping("/post-login")
    public ResponseEntity<Map<String, Object>> postLogin(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User is not authenticated"));
        }

        OAuth2AuthenticationToken oauth2Auth = (OAuth2AuthenticationToken) authentication;
        OidcUser oidcUser = (OidcUser) oauth2Auth.getPrincipal();

        Map<String, Object> response = new HashMap<>();
        response.put("username", oidcUser.getAttribute("cognito:username"));
        response.put("email", oidcUser.getAttribute("email"));
        response.put("cognito_uuid", oidcUser.getAttribute("sub"));
        response.put("access_token", oidcUser.getIdToken().getTokenValue());

        return ResponseEntity.ok(response);
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







}
