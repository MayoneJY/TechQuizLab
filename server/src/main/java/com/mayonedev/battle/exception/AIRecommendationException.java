package com.mayonedev.battle.exception;

import org.springframework.http.HttpStatus;

public class AIRecommendationException extends GlobalException {
    
    public AIRecommendationException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
    
    public AIRecommendationException(String message, Throwable cause) {
        super(HttpStatus.SERVICE_UNAVAILABLE, message);
        initCause(cause);
    }
}

