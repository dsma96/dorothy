package com.silverwing.dorothy.domain.Exception;

public class CouponException extends RuntimeException{
    public CouponException(String message) {
        super(message);
    }

    public CouponException(String message, Throwable cause) {
        super(message, cause);
    }
}
