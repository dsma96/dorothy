package com.silverwing.dorothy.domain.Exception;

public class ReserveException extends RuntimeException {
    public ReserveException(String message) {
        super(message);
    }

    public ReserveException(String message, Throwable cause) {
        super(message, cause);
    }
}
