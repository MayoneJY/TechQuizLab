package com.mayonedev.battle.domain.gamification.service;

import com.mayonedev.battle.domain.gamification.entity.Achievement;
import com.mayonedev.battle.domain.gamification.entity.UserAchievement;
import com.mayonedev.battle.domain.gamification.entity.UserDailyMission;
import com.mayonedev.battle.domain.gamification.dto.RankingDTO;
import com.mayonedev.battle.domain.gamification.dto.FriendDTO;
import java.util.List;

public interface GamificationService {
    List<Achievement> getAllAchievements();

    List<UserAchievement> getUserAchievements(Long userId);

    void checkAchievements(Long userId);

    List<UserDailyMission> getDailyMissions(Long userId);

    void claimMissionReward(Long userId, Long missionId);

    List<RankingDTO> getRankings();

    List<FriendDTO> getFriends(Long userId);

    void addFriend(Long userId, String nickname);

    void completeMockInterview(Long userId, Long stageId);

    void completeMission(Long userId, String missionType);
}
