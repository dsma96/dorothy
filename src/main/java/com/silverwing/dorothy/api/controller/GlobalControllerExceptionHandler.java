package com.silverwing.dorothy.api.controller;

import com.silverwing.dorothy.domain.Exception.ReserveException;
import com.silverwing.dorothy.domain.Exception.UserException;
import com.silverwing.dorothy.domain.Exception.VerifyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
class GlobalControllerExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity< ResponseData> handleConflict(Exception e) {
        return new ResponseEntity<>(new ResponseData<>(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity< ResponseData> handleAuthentication(Exception e) {
        return new ResponseEntity<>(new ResponseData<>(e.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ReserveException.class)
    public ResponseEntity< ResponseData> handleReserveException(Exception e) {
        return new ResponseEntity<>(new ResponseData<>(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity< ResponseData> handleUserException(Exception e) {
        return new ResponseEntity<>(new ResponseData<>(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(VerifyException.class)
    public ResponseEntity< ResponseData> handleVerifyException(Exception e) {
        return new ResponseEntity<>(new ResponseData<>(e.getMessage()), HttpStatus.NOT_ACCEPTABLE);
    }
}