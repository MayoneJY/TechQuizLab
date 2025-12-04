package com.mayonedev.battle.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mayonedev.battle.dto.LoginResponseDTO;
import com.mayonedev.battle.dto.UserRefreshTokenDTO;
import com.mayonedev.battle.dto.UserRefreshTokenUpdateDTO;
import com.mayonedev.battle.entity.User;
import com.mayonedev.battle.entity.UserRefreshToken;
import com.mayonedev.battle.exception.GlobalException;
import com.mayonedev.battle.security.TokenProvider;
import com.mayonedev.battle.security.ValidToken;
import com.mayonedev.battle.service.UserTokenService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/token")
@RequiredArgsConstructor
public class TokenContoller {

    private final UserTokenService userTokenService;

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue("refreshToken") String refreshToken, HttpServletResponse response) {
        if(refreshToken == null || refreshToken.isBlank()) {
            throw new GlobalException(HttpStatus.UNAUTHORIZED, "REFRESH_TOKEN_NOT_FOUND");
        }

        ValidToken validToken = TokenProvider.isValidToken(refreshToken);
        if(!validToken.isValid()) {
            throw new GlobalException(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN");
        }
        
        UserRefreshToken userRefreshToken = userTokenService.findByRefreshToken(refreshToken);
        if(userRefreshToken == null) {
            throw new GlobalException(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN");
        }

        if(userRefreshToken.isRevoked()) {
            // TODO: Refresh Token이 이미 사용되었는데 또 사용하려고 할 때
            throw new GlobalException(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN");
        }

        String userEmail = TokenProvider.getClaimsToUserEmail(refreshToken);
        String userId = TokenProvider.getClaimsToUserId(refreshToken);
        String userRole = TokenProvider.getClaimsToUserRole(refreshToken);
        
        User user = User.builder()
                .email(userEmail)
                .id(Long.parseLong(userId))
                .role(userRole)
                .build();

        UserRefreshTokenUpdateDTO userRefreshTokenUpdateDTO = UserRefreshTokenUpdateDTO.builder()
                .refreshToken(refreshToken)
                .revoked(true)
                .build();
        
        if(userTokenService.updateRefreshToken(userRefreshTokenUpdateDTO) == 0) {
            throw new GlobalException(HttpStatus.UNAUTHORIZED, "REFRESH_TOKEN_UPDATE_FAILED");
        }

        String newAccessToken = TokenProvider.generateJWT(user, true);
        String newRefreshToken = TokenProvider.generateJWT(user, false);

        UserRefreshTokenDTO newUserRefreshTokenDTO = UserRefreshTokenDTO.builder()
                .userId(userRefreshToken.getUserId())
                .validity(LocalDateTime.now().plusDays(7))
                .refreshToken(newRefreshToken)
                .build();

        if(userTokenService.insertRefreshToken(newUserRefreshTokenDTO) == 0) {
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
