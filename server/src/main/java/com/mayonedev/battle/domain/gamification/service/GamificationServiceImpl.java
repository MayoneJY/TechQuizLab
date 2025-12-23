package com.mayonedev.battle.domain.gamification.service;

import com.mayonedev.battle.domain.gamification.dao.AchievementDao;
import com.mayonedev.battle.domain.gamification.dao.DailyMissionDao;
import com.mayonedev.battle.domain.gamification.dao.UserAchievementDao;
import com.mayonedev.battle.domain.gamification.dao.UserDailyMissionDao;
import com.mayonedev.battle.domain.gamification.dao.DailyLogDao;
import com.mayonedev.battle.domain.gamification.dao.InterviewHistoryDao;
import com.mayonedev.battle.domain.user.dao.UserDao;
import com.mayonedev.battle.domain.user.dao.FriendDao;
import com.mayonedev.battle.domain.gamification.dto.FriendDTO;
import com.mayonedev.battle.domain.gamification.dto.RankingDTO;
import com.mayonedev.battle.domain.gamification.entity.Achievement;
import com.mayonedev.battle.domain.gamification.entity.DailyMission;
import com.mayonedev.battle.domain.gamification.entity.DailyLog;
import com.mayonedev.battle.domain.gamification.entity.InterviewHistory;
import com.mayonedev.battle.domain.user.entity.User;
import com.mayonedev.battle.domain.user.entity.Friend;
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
    private final DailyLogDao dailyLogDao;
    private final InterviewHistoryDao interviewHistoryDao;

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
        List<UserDailyMission> userMissions = userDailyMissionDao.findByUserIdAndDate(userId, today);
        List<DailyMission> allActiveMissions = dailyMissionDao.findAll();

        // Create a set of already assigned mission IDs
        List<Integer> assignedMissionIds = userMissions.stream()
                .map(UserDailyMission::getMissionId)
                .collect(Collectors.toList());

        boolean isUpdated = false;

        for (DailyMission dm : allActiveMissions) {
            if (!assignedMissionIds.contains(dm.getMissionId())) {
                // Determine if this is a new assignment or synchronization
                if (userMissions.isEmpty()) {
                    System.out.println("SERVICE: New day mission assignment for userId=" + userId);
                } else {
                    System.out.println("SERVICE: Syncing missing mission " + dm.getTitle() + " for userId=" + userId);
                }

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
                            grantExpAndLevelUp(user, Long.valueOf(dm.getRewardExp()));
                        }
                    } catch (Exception e) {
                        log.error("Failed to auto-reward mission", e);
                    }
                } else {
                    udm.setCurrentCount(0);
                    udm.setIsCompleted(false);
                    udm.setIsRewarded(false);
                }

                udm.setCreatedAt(LocalDateTime.now());

                userDailyMissionDao.insert(udm);
                // Also set the mission object for return
                udm.setMission(dm);
                userMissions.add(udm);
                isUpdated = true;
            }
        }

        // If we updated the list, we might want to re-sort it based on completion
        // status to match the DB order
        // But since the frontend uses the list returned here, and we just appended to
        // it...
        // The DB query has ORDER BY, but our local list 'userMissions' is now mixed (DB
        // part sorted + new part appended).
        // It's better to sort it here to ensure consistency with the user's expectation
        // (uncompleted first).
        if (isUpdated) {
            userMissions.sort((a, b) -> {
                if (a.getIsCompleted() == b.getIsCompleted()) {
                    return Integer.compare(a.getMissionId(), b.getMissionId());
                }
                return Boolean.compare(a.getIsCompleted(), b.getIsCompleted());
            });
        }

        return userMissions;
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

    @Override
    @Transactional
    public void addFriend(Long userId, String nickname) {
        User targetUser = userDao.findByNickname(nickname);
        if (targetUser == null) {
            throw new RuntimeException("해당 닉네임의 유저를 찾을 수 없습니다.");
        }
        if (targetUser.getUserId().equals(userId)) {
            throw new RuntimeException("자기 자신을 친구로 추가할 수 없습니다.");
        }

        try {
            Friend friend = new Friend();
            friend.setUserId(userId);
            friend.setFriendId(targetUser.getUserId());
            friend.setCreatedAt(LocalDateTime.now());
            friendDao.insert(friend);
        } catch (Exception e) {
            throw new RuntimeException("이미 등록된 친구이거나 친구 추가에 실패했습니다.");
        }
    }

    @Override
    @Transactional
    public void completeMockInterview(Long userId, Long stageId) {
        // 1. Record History
        try {
            InterviewHistory history = new InterviewHistory();
            history.setUserId(userId);
            history.setStageId(stageId);
            history.setCompletedAt(LocalDateTime.now());
            interviewHistoryDao.insert(history);
        } catch (Exception e) {
            log.error("Failed to record interview history", e);
        }
        // 2. Increment User Solved Count
        try {
            User user = userDao.findById(userId);
            if (user != null) {
                user.setSolvedCount(user.getSolvedCount() == null ? 1 : user.getSolvedCount() + 1);
                userDao.update(user);
            }
        } catch (Exception e) {
            log.error("Failed to increment user solved count", e);
        }

        // 3. Daily Mission Check
        LocalDate today = LocalDate.now();
        List<UserDailyMission> missions = userDailyMissionDao.findByUserIdAndDate(userId, today);

        if (missions.isEmpty()) {
            missions = getDailyMissions(userId);
        }

        for (UserDailyMission udm : missions) {
            DailyMission dm = udm.getMission();
            // Assuming Mission ID 2 "모의면접 3회 완료" is BATTLE_PLAY
            if ("BATTLE_PLAY".equalsIgnoreCase(dm.getMissionType()) && !udm.getIsCompleted()) {
                int newCount = udm.getCurrentCount() + 1;
                udm.setCurrentCount(newCount);

                boolean completed = newCount >= dm.getGoalCount();
                udm.setIsCompleted(completed);

                if (completed) {
                    udm.setIsRewarded(true); // Auto-reward

                    // Grant User Exp
                    int reward = dm.getRewardExp();
                    User user = userDao.findById(userId);
                    grantExpAndLevelUp(user, Long.valueOf(reward));

                    // Update Daily Log
                    DailyLog dailyLog = dailyLogDao.findByUserAndDate(userId, today);
                    if (dailyLog == null) {
                        dailyLog = new DailyLog(userId, today, true, reward);
                        dailyLogDao.insert(dailyLog);
                    } else {
                        dailyLog.setDailyExp(dailyLog.getDailyExp() + reward);
                        dailyLogDao.update(dailyLog);
                    }

                    userDailyMissionDao.updateRewardStatus(userId, today, udm.getMissionId(), true);
                }

                // Update Progress always (IsCompleted will be updated here too)
                userDailyMissionDao.updateProgress(udm);
            }
        }
    }

    @Override
    @Transactional
    public void completeMission(Long userId, String missionType) {
        LocalDate today = LocalDate.now();
        List<UserDailyMission> missions = userDailyMissionDao.findByUserIdAndDate(userId, today);

        if (missions.isEmpty()) {
            missions = getDailyMissions(userId);
        }

        for (UserDailyMission udm : missions) {
            DailyMission dm = udm.getMission();
            if (missionType.equalsIgnoreCase(dm.getMissionType()) && !udm.getIsCompleted()) {
                int newCount = udm.getCurrentCount() + 1;
                udm.setCurrentCount(newCount);

                boolean completed = newCount >= dm.getGoalCount();
                udm.setIsCompleted(completed);

                if (completed) {
                    udm.setIsRewarded(true); // Auto-reward

                    // Grant User Exp
                    int reward = dm.getRewardExp();
                    User user = userDao.findById(userId);
                    grantExpAndLevelUp(user, Long.valueOf(reward));

                    // Update Daily Log
                    DailyLog dailyLog = dailyLogDao.findByUserAndDate(userId, today);
                    if (dailyLog == null) {
                        dailyLog = new DailyLog(userId, today, true, reward);
                        dailyLogDao.insert(dailyLog);
                    } else {
                        dailyLog.setDailyExp(dailyLog.getDailyExp() + reward);
                        dailyLogDao.update(dailyLog);
                    }

                    userDailyMissionDao.updateRewardStatus(userId, today, udm.getMissionId(), true);
                }

                // Update Progress
                userDailyMissionDao.updateProgress(udm);
            }
        }
    }
}
