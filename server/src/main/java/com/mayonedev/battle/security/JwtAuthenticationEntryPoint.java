package com.mayonedev.battle.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mayonedev.battle.dto.ApiResponse;
import com.mayonedev.battle.exception.JwtAuthenticationException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        log.info("인증 실패");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=utf-8");

        String message = resolveMessage(authException);

        ApiResponse<Object> apiResponse = ApiResponse.error(message);

        String json = objectMapper.writeValueAsString(apiResponse);

        response.getWriter().write(json);
    }

    private String resolveMessage(AuthenticationException authException) {
        if (authException instanceof JwtAuthenticationException) {
            String name = ((JwtAuthenticationException) authException).getErrorName();
            return switch (name) {
                case "TOKEN_NULL" -> "토큰이 존재하지 않습니다.";
                case "TOKEN_EXPIRED" -> "토큰이 만료되었습니다.";
                case "TOKEN_INVALID" -> "유효하지 않은 토큰입니다.";
                default -> "인증에 실패했습니다. 1";
            };
        }
        // log.info(authException.getMessage());
        return "인증에 실패했습니다. 2";
    }

}
