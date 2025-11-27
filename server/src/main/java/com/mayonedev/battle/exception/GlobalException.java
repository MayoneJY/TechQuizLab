package com.mayonedev.battle.exception;

import org.springframework.http.HttpStatus;

public class GlobalException extends RuntimeException {
    private HttpStatus httpStatus;
    private String message;

    public GlobalException(HttpStatus httpStatus, String message){
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
