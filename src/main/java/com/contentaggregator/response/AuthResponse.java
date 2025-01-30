package com.contentaggregator.response;

public class AuthResponse {
    private String idToken;
    private String accessToken;

    public AuthResponse(String idToken, String accessToken) {
        this.idToken = idToken;
        this.accessToken = accessToken;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
