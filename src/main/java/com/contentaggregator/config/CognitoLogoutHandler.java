package com.contentaggregator.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class CognitoLogoutHandler extends SimpleUrlLogoutSuccessHandler {

    private static final String COGNITO_LOGOUT_URL = "https://us-east-29qbfa8ryf.auth.us-east-2.amazoncognito.com/logout";
    private static final String CLIENT_ID = "5oncoq9mddhbmluooq6kpib2kj";
    private static final String LOGOUT_REDIRECT_URI = "http://localhost:8080"; // Redirect back to your site after logout

    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // Build the Cognito logout URL
        String logoutUrl = UriComponentsBuilder.fromUri(URI.create(COGNITO_LOGOUT_URL))
                .queryParam("client_id", CLIENT_ID)
                .queryParam("logout_uri", LOGOUT_REDIRECT_URI)
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUriString();

        // Redirect the user to Cognito logout
        response.sendRedirect(logoutUrl);
    }
}
