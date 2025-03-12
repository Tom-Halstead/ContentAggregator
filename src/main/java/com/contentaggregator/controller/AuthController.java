package com.contentaggregator.controller;

import com.contentaggregator.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final OAuth2AuthorizedClientService authorizedClientService;

    private final UserService userService;

    // ✅ Inject OAuth2AuthorizedClientService properly
    @Autowired
    public AuthController(OAuth2AuthorizedClientService authorizedClientService, UserService userService) {
        this.authorizedClientService = authorizedClientService;
        this.userService = userService;
    }


    @GetMapping("/post-login")
    public ResponseEntity<Void> postLogin(
            OAuth2AuthenticationToken authentication,
            @RequestHeader(value = "Referer", required = false) String referer,
            HttpServletResponse response) {

        Map<String, Object> attributes = authentication.getPrincipal().getAttributes();
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName()
        );

        String accessToken = (authorizedClient != null && authorizedClient.getAccessToken() != null)
                ? authorizedClient.getAccessToken().getTokenValue()
                : "no_access_token";

        String username = attributes.getOrDefault("username", "unknown_user").toString();
        String email = attributes.getOrDefault("email", "unknown_email").toString();
        String cognitoUuid = attributes.getOrDefault("sub", "unknown_uuid").toString();

        userService.saveOrUpdateUser(username, email, cognitoUuid);


        String finalRedirectUrl = "http://localhost:8080/legacy/index.html";


        finalRedirectUrl = String.format("%s?username=%s&email=%s&access_token=%s",
                finalRedirectUrl, username, email, accessToken);

        response.setHeader("Location", finalRedirectUrl);
        return ResponseEntity.status(HttpStatus.FOUND).build(); // 302 Redirect
    }


    @GetMapping("/user-info")
    public ResponseEntity<Map<String, Object>> getUserInfo(@AuthenticationPrincipal OidcUser oidcUser) {
        if (oidcUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // ✅ Retrieve user details from the ID token
        String username = oidcUser.getAttribute("cognito:username");
        String email = oidcUser.getAttribute("email");
        String accessToken = oidcUser.getIdToken().getTokenValue();

        return ResponseEntity.ok(Map.of(
                "username", username,
                "email", email,
                "access_token", accessToken
        ));
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
