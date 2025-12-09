package com.mayonedev.battle.domain.gamification.service;

import com.mayonedev.battle.domain.gamification.dao.AchievementDao;
import com.mayonedev.battle.domain.gamification.dao.DailyMissionDao;
import com.mayonedev.battle.domain.gamification.dao.UserAchievementDao;
import com.mayonedev.battle.domain.gamification.dao.UserDailyMissionDao;
import com.mayonedev.battle.domain.user.dao.UserDao;
import com.mayonedev.battle.domain.user.dao.FriendDao;
import com.mayonedev.battle.domain.gamification.dto.FriendDTO;
import com.mayonedev.battle.domain.gamification.dto.RankingDTO;
import com.mayonedev.battle.domain.gamification.entity.Achievement;
import com.mayonedev.battle.domain.gamification.entity.DailyMission;
import com.mayonedev.battle.domain.user.entity.User;
import com.mayonedev.battle.domain.gamification.entity.UserAchievement;
import com.mayonedev.battle.domain.gamification.entity.UserDailyMission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class GamificationServiceImpl implements GamificationService {

    private final AchievementDao achievementDao;
    private final UserAchievementDao userAchievementDao;
    private final DailyMissionDao dailyMissionDao;
    private final UserDailyMissionDao userDailyMissionDao;
    private final UserDao userDao;
    private final FriendDao friendDao;

    @Override
    public List<Achievement> getAllAchievements() {
        return Collections.emptyList();
    }

    @Override
    public List<UserAchievement> getUserAchievements(Long userId) {
        return Collections.emptyList();
    }

    @Override
    public void checkAchievements(Long userId) {
        // Achievement system disabled request
    }

    @Override
    @Transactional
    public List<UserDailyMission> getDailyMissions(Long userId) {
        LocalDate today = LocalDate.now();
        List<UserDailyMission> missions = userDailyMissionDao.findByUserIdAndDate(userId, today);

        if (missions.isEmpty()) {
            System.out.println("SERVICE: No existing missions. Creating new ones for userId=" + userId);
            // Assign missions
            List<DailyMission> allMissions = dailyMissionDao.findAll();
            // Randomly pick 3
            for (int i = 0; i < Math.min(3, allMissions.size()); i++) {
                DailyMission dm = allMissions.get(i);
                UserDailyMission udm = new UserDailyMission();
                udm.setUserId(userId);
                udm.setMissionDate(today);
                udm.setMissionId(dm.getMissionId());
                // Auto-complete if type is LOGIN or ATTENDANCE
                if ("LOGIN".equalsIgnoreCase(dm.getMissionType())
                        || "ATTENDANCE".equalsIgnoreCase(dm.getMissionType())) {
                    udm.setCurrentCount(dm.getGoalCount());
                    udm.setIsCompleted(true);
                    udm.setIsRewarded(true);

                    // Auto-reward logic
                    try {
                        User user = userDao.findById(userId);
                        if (user != null) {
                            System.out.println("SERVICE: Auto-rewarding mission " + dm.getTitle());
                            grantExpAndLevelUp(user, Long.valueOf(dm.getRewardExp()));
                            log.info("Auto-rewarded mission: {} for user: {}", dm.getTitle(), userId);
                        } else {
                            log.error("User not found for auto-reward: {}", userId);
                        }
                    } catch (Exception e) {
                        System.err.println("SERVICE ERROR: Auto-reward failed: " + e.getMessage());
                        e.printStackTrace();
                        log.error("Failed to auto-reward mission", e);
                        // Do not fail the whole request, just log
                    }
                } else {
                    udm.setCurrentCount(0);
                    udm.setIsCompleted(false);
                    udm.setIsRewarded(false);
                }

                udm.setCreatedAt(LocalDateTime.now());

                userDailyMissionDao.insert(udm);
                // Also set the mission object for return if needed
                udm.setMission(dm);
                missions.add(udm);
            }
        }
        return missions;
    }

    private void grantExpAndLevelUp(User user, Long expAmount) {
        long currentExp = user.getExp() + expAmount;
        int currentLevel = user.getLevel();
        long maxExp = currentLevel * 1000L;

        // Level up logic
        while (currentExp >= maxExp) {
            currentExp -= maxExp;
            currentLevel++;
            maxExp = currentLevel * 1000L;
            log.info("Level Up! New Level: {}, Remaining Exp: {}", currentLevel, currentExp);
        }

        user.setExp(currentExp);
        user.setLevel(currentLevel);
        userDao.update(user);
        log.info("User updated. New Level: {}, New Exp: {}", user.getLevel(), user.getExp());
    }

    @Override
    @Transactional
    public void claimMissionReward(Long userId, Long missionId) {
        System.out.println("SERVICE HIT: claimMissionReward userId=" + userId + ", missionId=" + missionId);
        log.info("Attempting to claim reward. UserId: {}, MissionId: {}", userId, missionId);
        LocalDate today = LocalDate.now();

        UserDailyMission userMission = userDailyMissionDao.findByCompositeKey(userId, today, missionId.intValue());

        System.out.println("SERVICE: Fetched UserMission: " + userMission);
        log.info("UserMission fetched: {}", userMission);

        if (userMission != null && userMission.getIsCompleted() && !userMission.getIsRewarded()) {
            int updatedRows = userDailyMissionDao.updateRewardStatus(userId, today, missionId.intValue(), true);
            System.out.println("SERVICE: Updated Reward Status Rows: " + updatedRows);
            log.info("Updated reward status. Rows affected: {}", updatedRows);

            DailyMission mission = dailyMissionDao.findById(missionId.intValue());
            log.info("DailyMission fetched: {}", mission);

            if (mission != null) {
                User user = userDao.findById(userId);
                log.info("User before update - Level: {}, Exp: {}", user.getLevel(), user.getExp());

                grantExpAndLevelUp(user, Long.valueOf(mission.getRewardExp()));
            }
        } else {
            System.out.println("SERVICE: Claim Failed. userMission is null or invalid state.");
            log.warn("Claim failed. UserMission mismatch or already rewarded.");
        }
    }

    @Override
    public List<RankingDTO> getRankings() {
        AtomicInteger rankCounter = new AtomicInteger(1);
        return userDao.findRankings().stream()
                .map(user -> RankingDTO.builder()
                        .userId(user.getUserId())
                        .nickname(user.getNickname())
                        .level(user.getLevel())
                        .exp(user.getExp())
                        .rank(rankCounter.getAndIncrement())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<FriendDTO> getFriends(Long userId) {
        return friendDao.findFriendsWithDetails(userId);
    }
}
