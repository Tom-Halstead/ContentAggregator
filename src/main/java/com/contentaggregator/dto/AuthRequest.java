package com.contentaggregator.dto;

public class AuthRequest {
    private String code;

    public AuthRequest() {}

    public AuthRequest(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "AuthRequest{" +
                "code='" + code + '\'' +
                '}';
    }
}
