package com.mayonedev.battle.domain.auth.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mayonedev.battle.domain.auth.dto.LoginResponseDTO;
import com.mayonedev.battle.domain.auth.dto.UserRefreshTokenDTO;
import com.mayonedev.battle.domain.auth.entity.RefreshToken;
import com.mayonedev.battle.domain.user.entity.User;
import com.mayonedev.battle.exception.GlobalException;
import com.mayonedev.battle.security.TokenProvider;
import com.mayonedev.battle.security.ValidToken;
import com.mayonedev.battle.domain.auth.service.UserTokenService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/token")
@RequiredArgsConstructor
public class TokenContoller {

    private final UserTokenService userTokenService;

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue("refreshToken") String refreshToken,
            HttpServletResponse response) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new GlobalException(HttpStatus.UNAUTHORIZED, "REFRESH_TOKEN_NOT_FOUND");
        }

        ValidToken validToken = TokenProvider.isValidToken(refreshToken);
        if (!validToken.isValid()) {
            throw new GlobalException(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN");
        }

        RefreshToken storedToken = userTokenService.findByRefreshToken(refreshToken);
        if (storedToken == null) {
            throw new GlobalException(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN");
        }

        if (storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            userTokenService.deleteByRefreshToken(refreshToken);
            throw new GlobalException(HttpStatus.UNAUTHORIZED, "EXPIRED_TOKEN");
        }

        String userEmail = TokenProvider.getClaimsToUserEmail(refreshToken);
        String userId = TokenProvider.getClaimsToUserId(refreshToken);
        String userRole = TokenProvider.getClaimsToUserRole(refreshToken);

        User user = User.builder()
                .email(userEmail)
                .userId(Long.parseLong(userId))
                .role(userRole)
                .build();

        // Rotate token: delete old one
        userTokenService.deleteByRefreshToken(refreshToken);

        String newAccessToken = TokenProvider.generateJWT(user, true);
        String newRefreshToken = TokenProvider.generateJWT(user, false);

        UserRefreshTokenDTO newUserRefreshTokenDTO = UserRefreshTokenDTO.builder()
                .userId(storedToken.getUserId())
                .expiresAt(LocalDateTime.now().plusDays(7))
                .refreshToken(newRefreshToken)
                .build();

        if (userTokenService.insertRefreshToken(newUserRefreshTokenDTO) == 0) {
            throw new GlobalException(HttpStatus.UNAUTHORIZED, "REFRESH_TOKEN_INSERT_FAILED");
        }

        Cookie refreshTokenCookie = new Cookie("refreshToken", newRefreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/api/token/refresh");
        refreshTokenCookie.setMaxAge(60 * 60 * 24 * 7); // 7일
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok().body(new LoginResponseDTO(newAccessToken));
    }
}
