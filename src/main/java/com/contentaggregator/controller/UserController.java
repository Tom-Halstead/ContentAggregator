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
            return ResponseEntity.status(401).body(Map.of("error", "User is not authenticated"));
        }

        if (authentication instanceof OAuth2AuthenticationToken oauth2Auth) {
            OidcUser oidcUser = (OidcUser) oauth2Auth.getPrincipal();
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("username", oidcUser.getAttribute("cognito:username"));
            userInfo.put("email", oidcUser.getAttribute("email"));
            userInfo.put("cognito_uuid", oidcUser.getAttribute("sub"));

            return ResponseEntity.ok(userInfo);
        }

        return ResponseEntity.status(400).body(Map.of("error", "Invalid authentication type"));
    }





}
