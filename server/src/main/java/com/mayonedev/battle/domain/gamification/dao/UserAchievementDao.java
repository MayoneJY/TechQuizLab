package com.mayonedev.battle.domain.gamification.dao;

import com.mayonedev.battle.domain.gamification.entity.UserAchievement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UserAchievementDao {
    List<UserAchievement> findByUserId(Long userId);
    int insert(UserAchievement userAchievement);
    boolean existsByUserIdAndAchievementId(@Param("userId") Long userId, @Param("achievementId") Long achievementId);
}
