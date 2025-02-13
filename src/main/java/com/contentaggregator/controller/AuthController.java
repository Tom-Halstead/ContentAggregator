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
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
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
@RequestMapping
public class AuthController {

    @Autowired
    private final OAuth2AuthorizedClientService authorizedClientService;

    public AuthController(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }


    @GetMapping("/post-login")
    public ResponseEntity<Void> postLogin(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (!(authentication instanceof OAuth2AuthenticationToken oauth2Auth)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // ✅ Correct way to get the access token
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                oauth2Auth.getAuthorizedClientRegistrationId(),
                oauth2Auth.getName()
        );

        if (client == null || client.getAccessToken() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String accessToken = client.getAccessToken().getTokenValue();

        // ✅ Retrieve user details
        OidcUser oidcUser = (OidcUser) oauth2Auth.getPrincipal();
        String username = oidcUser.getAttribute("cognito:username");
        String email = oidcUser.getAttribute("email");
        String cognitoUuid = oidcUser.getAttribute("sub");

        // ✅ Store details in cookies (for frontend use)
        response.addCookie(createCookie("username", username, false));
        response.addCookie(createCookie("email", email, false));
        response.addCookie(createCookie("access_token", accessToken, true)); // Secure access token

        // ✅ Redirect user to frontend
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header("Location", "http://127.0.0.1:5500/index.html")
                .build();
    }

    private Cookie createCookie(String name, String value, boolean httpOnly) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(httpOnly); // Restrict access to JavaScript if true
        cookie.setMaxAge(60 * 60); // 1 hour
        return cookie;
    }


    @PostMapping("/post-logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = new Cookie("access_token", null); // Nullify the cookie
        cookie.setHttpOnly(false);
        cookie.setSecure(true); // Match security settings
        cookie.setPath("/");
        cookie.setMaxAge(0); // Expire the cookie immediately
        response.addCookie(cookie);
        return ResponseEntity.ok("Logged out successfully.");
    }







}
