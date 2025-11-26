package com.mayonedev.battle.security;

import lombok.Builder;

public class ValidToken {
    private final boolean isValid;
    private final String errorName;

    @Builder
    public ValidToken(boolean isValid, String errorName) {
        this.isValid = isValid;
        this.errorName = errorName;
    }

    public boolean isValid() {
        return isValid;
    }

    public String getErrorName() {
        return errorName;
    }
}
