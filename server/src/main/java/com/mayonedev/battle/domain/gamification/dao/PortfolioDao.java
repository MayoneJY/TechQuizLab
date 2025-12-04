package com.mayonedev.battle.domain.gamification.dao;

import com.mayonedev.battle.domain.gamification.entity.Portfolio;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PortfolioDao {
    void insert(Portfolio portfolio);

    List<Portfolio> findAllByUserId(Long userId);

    Portfolio findByUserAndPfId(@Param("userId") Long userId, @Param("pfId") Long pfId);
}
