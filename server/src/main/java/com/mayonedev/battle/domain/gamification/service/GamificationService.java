package com.mayonedev.battle.domain.gamification.service;

import com.mayonedev.battle.domain.gamification.entity.Achievement;
import com.mayonedev.battle.domain.gamification.entity.UserAchievement;
import com.mayonedev.battle.domain.gamification.entity.UserDailyMission;
import java.util.List;

public interface GamificationService {
    List<Achievement> getAllAchievements();
    List<UserAchievement> getUserAchievements(Long userId);
    void checkAchievements(Long userId);
    
    List<UserDailyMission> getDailyMissions(Long userId);
    void claimMissionReward(Long userMissionId);
}
