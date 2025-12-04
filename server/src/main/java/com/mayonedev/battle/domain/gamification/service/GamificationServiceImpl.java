package com.mayonedev.battle.domain.gamification.service;

import com.mayonedev.battle.domain.gamification.dao.AchievementDao;
import com.mayonedev.battle.domain.gamification.dao.DailyMissionDao;
import com.mayonedev.battle.domain.gamification.dao.UserAchievementDao;
import com.mayonedev.battle.domain.gamification.dao.UserDailyMissionDao;
import com.mayonedev.battle.domain.user.dao.UserDao;
import com.mayonedev.battle.domain.gamification.entity.Achievement;
import com.mayonedev.battle.domain.gamification.entity.DailyMission;
import com.mayonedev.battle.domain.user.entity.User;
import com.mayonedev.battle.domain.gamification.entity.UserAchievement;
import com.mayonedev.battle.domain.gamification.entity.UserDailyMission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GamificationServiceImpl implements GamificationService {

    private final AchievementDao achievementDao;
    private final UserAchievementDao userAchievementDao;
    private final DailyMissionDao dailyMissionDao;
    private final UserDailyMissionDao userDailyMissionDao;
    private final UserDao userDao;

    @Override
    public List<Achievement> getAllAchievements() {
        return achievementDao.findAll();
    }

    @Override
    public List<UserAchievement> getUserAchievements(Long userId) {
        return userAchievementDao.findByUserId(userId);
    }

    @Override
    @Transactional
    public void checkAchievements(Long userId) {
        User user = userDao.findById(userId);
        List<Achievement> achievements = achievementDao.findAll();
        
        for (Achievement achievement : achievements) {
            if (userAchievementDao.existsByUserIdAndAchievementId(userId, achievement.getId())) {
                continue;
            }
            
            boolean achieved = false;
            if ("LEVEL_UP".equals(achievement.getConditionType())) {
                if (user.getLevel() >= achievement.getConditionValue()) {
                    achieved = true;
                }
            }
            // Add other conditions...
            
            if (achieved) {
                UserAchievement ua = new UserAchievement();
                ua.setUserId(userId);
                ua.setAchievementId(achievement.getId());
                ua.setAchievedAt(LocalDateTime.now());
                userAchievementDao.insert(ua);
                
                // Give reward
                user.setExp(user.getExp() + achievement.getRewardExp());
                userDao.update(user);
            }
        }
    }

    @Override
    public List<UserDailyMission> getDailyMissions(Long userId) {
        LocalDate today = LocalDate.now();
        List<UserDailyMission> missions = userDailyMissionDao.findByUserIdAndDate(userId, today);
        
        if (missions.isEmpty()) {
            // Assign missions
            List<DailyMission> allMissions = dailyMissionDao.findAll();
            // Randomly pick 3 (simplified: pick first 3)
            for (int i = 0; i < Math.min(3, allMissions.size()); i++) {
                DailyMission dm = allMissions.get(i);
                UserDailyMission udm = new UserDailyMission();
                udm.setUserId(userId);
                udm.setDailyMissionId(dm.getId());
                udm.setProgress(0);
                udm.setIsCompleted(false);
                udm.setIsClaimed(false);
                udm.setAssignedDate(today);
                userDailyMissionDao.insert(udm);
                missions.add(udm);
            }
        }
        return missions;
    }

    @Override
    @Transactional
    public void claimMissionReward(Long userMissionId) {
        // Implementation needed
        userDailyMissionDao.updateClaimed(userMissionId);
    }
}
