package com.contentaggregator.exception;

public class InvalidNewsRequestException extends RuntimeException {
    public InvalidNewsRequestException(String message) {
        super(message);
    }

    public InvalidNewsRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
