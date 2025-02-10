package com.contentaggregator.service;

import com.contentaggregator.config.CognitoConfig;
import com.contentaggregator.response.AuthResponse;
import com.contentaggregator.response.JwtTokenResponse;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;

@Service
public class AuthService {

    private final RestTemplate restTemplate;
    private final CognitoConfig cognitoConfig;

    @Autowired
    public AuthService(RestTemplate restTemplate, CognitoConfig cognitoConfig) {
        this.restTemplate = restTemplate;
        this.cognitoConfig = cognitoConfig;
    }

    public AuthResponse exchangeCodeForToken(String code) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", cognitoConfig.getClientId());
        params.add("code", code);
        params.add("redirect_uri", cognitoConfig.getRedirectUri());
        params.add("client_secret", cognitoConfig.getClientSecret());  // Ensure this is securely managed

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<JwtTokenResponse> response = restTemplate.postForEntity(
                cognitoConfig.getTokenEndpoint(),
                request,
                JwtTokenResponse.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            JwtTokenResponse tokenResponse = response.getBody();
            return new AuthResponse(tokenResponse.getIdToken(), tokenResponse.getAccessToken());
        } else {
            throw new Exception("Failed to exchange code for tokens. Status: " + response.getStatusCode());
        }
    }

    public boolean validateToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            // Validate the token claims
            return validateClaims(claims);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean validateClaims(JWTClaimsSet claims) {
        // Implement claim validation logic, e.g., issuer, expiry
        return claims.getIssuer().equals(cognitoConfig.getIssuerUrl()) &&
                claims.getExpirationTime().after(new java.util.Date());
    }

    public JSONObject getUserInfo(String token) {
        // Decode token and extract user information
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            // Extract user info from claims
            JSONObject userInfo = new JSONObject();
            userInfo.put("username", claims.getSubject());
            userInfo.put("email", claims.getStringClaim("email"));
            return userInfo;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
