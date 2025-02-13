package com.contentaggregator.response;

public class AuthResponse {
    private String idToken;
    private String accessToken;
    private String name;
    private String email;

    public AuthResponse(String idToken, String accessToken, String name, String email) {
        this.idToken = idToken;
        this.accessToken = accessToken;
        this.name = name;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
