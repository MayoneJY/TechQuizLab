package com.mayonedev.battle.service;

import com.mayonedev.battle.entity.Achievement;
import com.mayonedev.battle.entity.UserAchievement;
import com.mayonedev.battle.entity.UserDailyMission;
import java.util.List;

public interface GamificationService {
    List<Achievement> getAllAchievements();
    List<UserAchievement> getUserAchievements(Long userId);
    void checkAchievements(Long userId);
    
    List<UserDailyMission> getDailyMissions(Long userId);
    void claimMissionReward(Long userMissionId);
}
