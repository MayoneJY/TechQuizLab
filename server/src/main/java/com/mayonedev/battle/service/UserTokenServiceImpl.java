package com.mayonedev.battle.service;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mayonedev.battle.dao.UserTokenDao;
import com.mayonedev.battle.dto.UserRefreshTokenDTO;
import com.mayonedev.battle.dto.UserRefreshTokenUpdateDTO;
import com.mayonedev.battle.entity.UserRefreshToken;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserTokenServiceImpl implements UserTokenService {
    private final UserTokenDao userTokenDao;

    @Override
    @Transactional
    public int insertRefreshToken(UserRefreshTokenDTO userRefreshTokenDTO) {
        log.info("refresh token 저장 - 사용자 ID: {}", userRefreshTokenDTO.getUserId());
        return userTokenDao.insertRefreshToken(userRefreshTokenDTO);
    }

    @Override
    public UserRefreshToken findByRefreshToken(@Param("refreshToken") String refreshToken) {
        log.info("refresh token 조회 - 사용자 ID: {}", refreshToken);
        return userTokenDao.findByRefreshToken(refreshToken);
    }

    @Override
    @Transactional
    public int deleteByRefreshToken(@Param("refreshToken") String refreshToken) {
        log.info("refresh token 삭제 - 사용자 ID: {}", refreshToken);
        return userTokenDao.deleteByRefreshToken(refreshToken);
    }

    @Override
    @Transactional
    public int updateRefreshToken(UserRefreshTokenUpdateDTO userRefreshTokenUpdateDTO) {
        return userTokenDao.updateRefreshToken(userRefreshTokenUpdateDTO);
    }
}
