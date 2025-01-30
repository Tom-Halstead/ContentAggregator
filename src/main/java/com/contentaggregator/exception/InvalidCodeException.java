package com.contentaggregator.exception;

public class InvalidCodeException extends RuntimeException {

    private final String code;

    public InvalidCodeException(String message, String code) {
        super(message);
        this.code = code;
    }

    public InvalidCodeException(String message, String code, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}

