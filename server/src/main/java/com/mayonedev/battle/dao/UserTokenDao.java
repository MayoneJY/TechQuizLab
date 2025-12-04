package com.mayonedev.battle.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.mayonedev.battle.dto.UserRefreshTokenDTO;
import com.mayonedev.battle.dto.UserRefreshTokenUpdateDTO;
import com.mayonedev.battle.entity.UserRefreshToken;

@Mapper
public interface UserTokenDao {
    /**
     * refresh token 저장
     */
    int insertRefreshToken(UserRefreshTokenDTO userRefreshToken);

    /**
     * refresh token 조회
     */
    UserRefreshToken findByRefreshToken(@Param("refreshToken") String refreshToken);

    /**
     * refresh token 삭제
     */
    int deleteByRefreshToken(@Param("refreshToken") String refreshToken);

    /**
     * refresh token 수정
     */
    int updateRefreshToken(UserRefreshTokenUpdateDTO userRefreshTokenUpdateDTO);
}
