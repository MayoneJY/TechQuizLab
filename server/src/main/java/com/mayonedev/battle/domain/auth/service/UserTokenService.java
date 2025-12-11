package com.mayonedev.battle.domain.auth.service;

import org.apache.ibatis.annotations.Param;

import com.mayonedev.battle.domain.auth.dto.UserRefreshTokenDTO;
import com.mayonedev.battle.domain.auth.dto.UserRefreshTokenUpdateDTO;
import com.mayonedev.battle.domain.auth.entity.RefreshToken;

public interface UserTokenService {
    /**
     * refresh token 저장
     */
    int insertRefreshToken(UserRefreshTokenDTO userRefreshTokenDTO);

    /**
     * refresh token 조회
     */
    RefreshToken findByRefreshToken(@Param("refreshToken") String refreshToken);

    /**
     * refresh token 삭제
     */
    int deleteByRefreshToken(@Param("refreshToken") String refreshToken);

    /**
     * refresh token 수정
     */
    int updateRefreshToken(UserRefreshTokenUpdateDTO userRefreshTokenUpdateDTO);
}
