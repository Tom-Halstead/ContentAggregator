package com.contentaggregator.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user-info")
public class UserController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> getUserInfo(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Authentication object cannot be null"));
        }

        try {
            Map<String, Object> response = new HashMap<>();

            // ✅ Case 1: Authenticated User (OAuth2)
            if (authentication instanceof OAuth2AuthenticationToken oauth2Auth) {
                OidcUser oidcUser = (OidcUser) oauth2Auth.getPrincipal();

                if (oidcUser == null) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(Map.of("error", "OAuth2 principal is null"));
                }

                // Extract useful attributes
                response.put("username", oidcUser.getAttribute("cognito:username"));
                response.put("email", oidcUser.getAttribute("email"));
                response.put("cognito_uuid", oidcUser.getAttribute("sub")); // Cognito unique user ID

                // ✅ Extract the JWT token from authentication context
                String accessToken = oidcUser.getIdToken().getTokenValue();
                response.put("access_token", accessToken); // ✅ Send JWT back to frontend

                return ResponseEntity.ok(response);
            }

            // ❌ Case 2: Anonymous Authentication (User not logged in)
            if (authentication instanceof AnonymousAuthenticationToken) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User is not authenticated"));
            }

            // ❌ Case 3: Unsupported Authentication Type
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Unsupported authentication type: " + authentication.getClass().getName()));

        } catch (Exception e) {
            // Log and return error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error retrieving user info", "details", e.getMessage()));
        }
    }





}
