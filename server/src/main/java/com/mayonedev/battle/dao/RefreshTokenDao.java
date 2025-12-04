package com.mayonedev.battle.dao;

import com.mayonedev.battle.entity.RefreshToken;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RefreshTokenDao {
    void insert(RefreshToken refreshToken);

    RefreshToken findByTokenValue(String tokenValue);

    void deleteByTokenValue(String tokenValue);
}
