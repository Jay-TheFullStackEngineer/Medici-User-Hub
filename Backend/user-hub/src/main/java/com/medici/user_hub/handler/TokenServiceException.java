package com.medici.user_hub.handler;

public class TokenServiceException extends RuntimeException {
    public TokenServiceException(String message) {
        super(message);
    }

    public TokenServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
