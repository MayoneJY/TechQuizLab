package com.mayonedev.battle.domain.gamification.dao;

import com.mayonedev.battle.domain.gamification.entity.Portfolio;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PortfolioDao {
    void insert(Portfolio portfolio);

    List<Portfolio> findPortfolios(@Param("userId") Long userId,
            @Param("search") String search,
            @Param("limit") int limit,
            @Param("offset") int offset);

    int countPortfolios(@Param("userId") Long userId, @Param("search") String search);

    Portfolio findByUserAndPfId(@Param("userId") Long userId, @Param("pfId") Long pfId);

    void update(Portfolio portfolio);

    void delete(@Param("userId") Long userId, @Param("pfId") Long pfId);

    Long findMaxPfIdByUserId(Long userId);

    List<Portfolio> findAllByUserId(Long userId);
}
