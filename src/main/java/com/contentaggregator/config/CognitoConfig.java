package com.contentaggregator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CognitoConfig {

    @Value("${CONTENT_AGGREGATOR_COGNITO_CLIENT_ID}")
    private String clientId;

    @Value("${CONTENT_AGGREGATOR_COGNITO_SECRET_KEY}")
    private String secretKey;

    @Value("${spring.security.oauth2.client.provider.cognito.issuer-uri}")
    private String issuerUrl;

    @Value("${spring.security.oauth2.client.registration.cognito.redirect-uri}")
    private String redirectUri;

    public String getClientId() {
        return clientId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getIssuerUrl() {
        return issuerUrl;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public String getTokenEndpoint() {
        return this.issuerUrl + "/oauth2/token";
    }
}
