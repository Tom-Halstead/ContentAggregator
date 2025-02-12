package com.contentaggregator.controller;

import jakarta.servlet.http.HttpSession;
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

import java.util.Map;

@RestController
@RequestMapping("/user-info")
public class UserController {

    public String getUserInfo(Authentication authentication) {
        if (authentication == null) {
            throw new IllegalArgumentException("Authentication object cannot be null");
        }

        try {
            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken oauth2Authentication = (OAuth2AuthenticationToken) authentication;
                // Logic to retrieve user info from the OAuth2 token
                // For example, extracting user details:
                if (oauth2Authentication.getPrincipal() == null) {
                    throw new RuntimeException("OAuth2 principal is null");
                }
                return "User info from OAuth2 token: " + oauth2Authentication.getPrincipal().getName();
            } else if (authentication instanceof AnonymousAuthenticationToken) {
                // Handle unauthenticated users
                return "User is not authenticated";
            } else {
                throw new UnsupportedOperationException("Unsupported authentication type: " + authentication.getClass().getName());
            }
        } catch (Exception e) {
            // Catch any other unexpected exceptions and log them (or rethrow as necessary)
            throw new RuntimeException("Error while retrieving user info", e);
        }
    }


}
