package com.silverwing.dorothy.domain.Exception;

public class VerifyException  extends RuntimeException {
    public VerifyException(String message) {
        super(message);
    }

    public VerifyException(String message, Throwable cause) {
        super(message, cause);
    }
}
