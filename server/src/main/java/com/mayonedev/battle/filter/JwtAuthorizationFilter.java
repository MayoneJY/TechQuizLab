package com.mayonedev.battle.filter;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mayonedev.battle.exception.GlobalException;
import com.mayonedev.battle.exception.JwtAuthenticationException;
import com.mayonedev.battle.security.JwtAuthenticationEntryPoint;
import com.mayonedev.battle.security.TokenProvider;
import com.mayonedev.battle.security.ValidToken;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private static final String ACCESS_TOKEN_HEADER_KEY = "Authorization";
    private static final List<String> WHITELIST_URLS = List.of(
            "/api/users/login", "/api/users/register");

    private UserDetailsService userDetailsService;
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    public JwtAuthorizationFilter(UserDetailsService userDetailsService,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("JWT Authorization Filter 진입");

        // Swagger UI URL은 토큰이 필요하지 않음
        if (request.getRequestURI().startsWith("/swagger-ui") ||
                request.getRequestURI().startsWith("/api-docs") ||
                request.getRequestURI().startsWith("/v3/api-docs") ||
                request.getRequestURI().startsWith("/swagger-ui.html")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 화이트리스트 URL은 토큰이 필요하지 않음
        if (WHITELIST_URLS.contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        try {

            String header = request.getHeader(ACCESS_TOKEN_HEADER_KEY);

            // Access Token이 존재하지 않다면
            if (StringUtils.isBlank(header)) {
                throw new JwtAuthenticationException("TOKEN_NULL");
            }
            log.info("Access Token 통과");

            String accessToken = TokenProvider.getHeaderToToken(header);
            ValidToken validToken = TokenProvider.isValidToken(accessToken);
            log.info("Access Token 검증");

            // Access Token이 만료되었다면
            if (!validToken.isValid()) {
                throw new JwtAuthenticationException(validToken.getErrorName());
            }
            log.info("Access Token 검증 통과");

            String email = TokenProvider.getClaimsToUserEmail(accessToken);
            log.info("Access Token email 검증");

            // Access Token이 유효하지 않다면
            if (StringUtils.isBlank(email)) {
                throw new JwtAuthenticationException("TOKEN_INVALID");
            }
            log.info("Access Token email 검증 통과");

            // Security Context에 Authentication이 존재하지 않는다면
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            log.info("Security Context 설정");
            filterChain.doFilter(request, response);
        } catch (JwtAuthenticationException e) {
            SecurityContextHolder.clearContext();
            jwtAuthenticationEntryPoint.commence(request, response, e);
        }
    }
}
