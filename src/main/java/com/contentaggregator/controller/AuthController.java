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
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private final OAuth2AuthorizedClientService authorizedClientService;

    public AuthController(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }


//    @GetMapping("/post-login")
//    public ResponseEntity<Map<String, Object>> postLogin(Authentication authentication) {
//        if (!(authentication instanceof OAuth2AuthenticationToken oauth2Auth)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//
//        // ✅ Fetch authorized client using the registered service
//        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
//                oauth2Auth.getAuthorizedClientRegistrationId(),
//                oauth2Auth.getName()
//        );
//
//        if (client == null || client.getAccessToken() == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//
//        String accessToken = client.getAccessToken().getTokenValue();
//
//        // ✅ Retrieve user details
//        OidcUser oidcUser = (OidcUser) oauth2Auth.getPrincipal();
//        String username = oidcUser.getAttribute("cognito:username");
//        String email = oidcUser.getAttribute("email");
//
//        // ✅ Return user details + access token to frontend
//        return ResponseEntity.ok(Map.of(
//                "username", username,
//                "email", email,
//                "access_token", accessToken
//        ));
//    }

    @GetMapping("/post-login")
    public ResponseEntity<Object> postLogin(OAuth2AuthenticationToken authentication) {
        try {
            // ✅ Get user attributes
            Map<String, Object> attributes = authentication.getPrincipal().getAttributes();

            // ✅ Print attributes for debugging
            System.out.println("User Attributes: " + attributes);

            // ✅ Retrieve attributes safely
            String username = (String) attributes.getOrDefault("username", attributes.getOrDefault("cognito:username", "unknown_user"));
            String email = (String) attributes.getOrDefault("email", "unknown_email");
            String accessToken = attributes.containsKey("access_token") ? (String) attributes.get("access_token") : "no_access_token";

            // ✅ Encode values for URL safety
            String encodedUsername = URLEncoder.encode(username, StandardCharsets.UTF_8);
            String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);
            String encodedAccessToken = URLEncoder.encode(accessToken, StandardCharsets.UTF_8);

            // ✅ Construct redirect URL to homepage with user details
            String redirectUrl = String.format("http://127.0.0.1:5500/src/main/resources/static/index.html?username=%s&email=%s&access_token=%s",
                    encodedUsername, encodedEmail, encodedAccessToken);

            System.out.println("Redirecting to: " + redirectUrl);

            // ✅ Redirect to homepage with user data
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(redirectUrl));

            return ResponseEntity.status(302).headers(headers).build();

        } catch (Exception e) {
            System.out.println("Error in postLogin: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Login error: " + e.getMessage());
        }
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
