package com.mayonedev.battle.domain.auth.service;

import com.mayonedev.battle.domain.auth.dao.RefreshTokenDao;
import com.mayonedev.battle.domain.auth.dto.UserRefreshTokenDTO;
import com.mayonedev.battle.domain.auth.dto.UserRefreshTokenUpdateDTO;
import com.mayonedev.battle.domain.auth.entity.RefreshToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserTokenServiceImpl implements UserTokenService {
    private final RefreshTokenDao refreshTokenDao;

    @Override
    @Transactional
    public int insertRefreshToken(UserRefreshTokenDTO dto) {
        log.info("refresh token 저장 - 사용자 ID: {}", dto.getUserId());
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(dto.getUserId());
        refreshToken.setTokenValue(dto.getRefreshToken());
        refreshToken.setExpiresAt(dto.getExpiresAt());
        refreshToken.setCreatedAt(LocalDateTime.now());
        // tokenId is not auto-increment, so we generate it manually.
        refreshToken.setTokenId(java.util.UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE);

        try {
            refreshTokenDao.insert(refreshToken);
            return 1;
        } catch (Exception e) {
            log.error("Error inserting refresh token", e);
            return 0;
        }
    }

    @Override
    public RefreshToken findByRefreshToken(String tokenValue) {
        log.info("refresh token 조회 - Token: {}", tokenValue);
        return refreshTokenDao.findByTokenValue(tokenValue);
    }

    @Override
    @Transactional
    public int deleteByRefreshToken(String tokenValue) {
        log.info("refresh token 삭제 - Token: {}", tokenValue);
        try {
            refreshTokenDao.deleteByTokenValue(tokenValue);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    @Transactional
    public int updateRefreshToken(UserRefreshTokenUpdateDTO dto) {
        // Not implemented in RefreshTokenDao yet, and maybe not needed if we just
        // insert/delete.
        // But if needed, we should add update method to DAO.
        // For now, let's return 0 or implement update if critical.
        // Usually refresh token rotation involves delete old, insert new.
        return 0;
    }
}
