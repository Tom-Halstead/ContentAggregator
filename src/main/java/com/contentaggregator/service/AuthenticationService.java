package com.contentaggregator.service;

import com.contentaggregator.config.CognitoConfig;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Service
public class AuthenticationService {

    @Autowired
    public AuthenticationService(CognitoConfig cognitoConfig) {
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
