package com.contentaggregator.service;

import com.contentaggregator.config.CognitoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.text.ParseException;

@Service
public class CognitoService {

    private final CognitoConfig cognitoConfig;

    @Autowired
    public CognitoService(CognitoConfig cognitoConfig) {
        this.cognitoConfig = cognitoConfig;
    }

    /**
     * Validates a JWT token's signature and checks its claims.
     *
     * @param token JWT token
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            // Example validation logic
            return claims.getIssuer().equals(cognitoConfig.getIssuerUrl()) &&
                    claims.getExpirationTime().after(new java.util.Date()) &&
                    claims.getAudience().contains(cognitoConfig.getClientId());
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Extracts user information from a JWT token.
     *
     * @param token JWT token
     * @return user information as a String
     */
    public String extractUserInfo(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            // Extracting some basic user information
            return String.format("User ID: %s, Email: %s", claims.getSubject(), claims.getClaim("email"));
        } catch (ParseException e) {
            e.printStackTrace();
            return "Failed to extract user info";
        }
    }
}
