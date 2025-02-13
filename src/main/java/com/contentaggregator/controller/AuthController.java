package com.contentaggregator.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {



    private final OAuth2AuthorizedClientService authorizedClientService;

    // ✅ Inject OAuth2AuthorizedClientService properly
    @Autowired
    public AuthController(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    @GetMapping("/post-login")
    public ResponseEntity<Void> postLogin(OAuth2AuthenticationToken authentication, HttpServletResponse response) {
        Map<String, Object> attributes = authentication.getPrincipal().getAttributes();
        System.out.println("User Attributes: " + attributes);

        // ✅ Fetch the authorized client and access token
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName()
        );

        String accessToken = (authorizedClient != null && authorizedClient.getAccessToken() != null)
                ? authorizedClient.getAccessToken().getTokenValue()
                : "no_access_token";

        System.out.println("Access Token: " + accessToken);

        String username = attributes.get("username") != null ? attributes.get("username").toString() : "unknown_user";
        String email = attributes.get("email") != null ? attributes.get("email").toString() : "unknown_email";

        // ✅ Construct the correct redirect URL
        String redirectUrl = String.format("http://127.0.0.1:5500/src/main/resources/static/index.html?username=%s&email=%s&access_token=%s",
                username, email, accessToken);

        System.out.println("Redirecting to: " + redirectUrl);

        response.setHeader("Location", redirectUrl);
        return ResponseEntity.status(HttpStatus.FOUND).build(); // 302 Redirect
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
