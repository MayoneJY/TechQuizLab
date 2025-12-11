package com.mayonedev.battle.exception;

import org.springframework.security.core.AuthenticationException;

public class JwtAuthenticationException extends AuthenticationException {

    private final String errorName;

    public JwtAuthenticationException(String errorName) {
        super(errorName);
        this.errorName = errorName;
    }

    public JwtAuthenticationException(String errorName, Throwable cause) {
        super(errorName, cause);
        this.errorName = errorName;
    }

    public String getErrorName() {
        return errorName;
    }
}
