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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.naming.AuthenticationException;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;


    // Autowire for secured info.
    @Autowired
    private CognitoConfig cognitoConfig;


    @GetMapping("/redirect-to-cognito")
    public String redirectToCognito() {
        return "redirect:" + buildCognitoUrl();
    }

    private String buildCognitoUrl() {
        return UriComponentsBuilder
                .fromUriString(cognitoConfig.getDomain() + "/oauth2/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", cognitoConfig.getClientId())
                .queryParam("scope", cognitoConfig.getScope())
                .queryParam("redirect_uri", cognitoConfig.getRedirectUri())
                .toUriString();
    }

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
    public ResponseEntity<AuthResponse> exchangeCodeForTokens(@RequestBody AuthRequest authRequest) {
        String code = authRequest.getCode(); // Extract code from the DTO
        try {
            AuthResponse authResponse = authService.exchangeCodeForToken(code);
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthResponse(null, "Failed to exchange code: " + e.getMessage()));
        }
    }


    @GetMapping("/callback")
    public ResponseEntity<?> handleCallback(@RequestParam Map<String, String> params) {
        if (params.containsKey("error")) {
            // Log the error or inform the user
            return ResponseEntity.badRequest().body("Error during OAuth2 callback: " + params.get("error_description"));
        }

        try {
            AuthResponse authResponse = authService.exchangeCodeForToken(params.get("code"));
            // Process tokens, set cookies, etc.
            HttpHeaders headers = new HttpHeaders();
            headers.add("Set-Cookie", "access_token=" + authResponse.getAccessToken() + "; HttpOnly; Path=/");
            return ResponseEntity.ok().headers(headers).body(authResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to exchange code for tokens: " + e.getMessage());
        }
    }


}
