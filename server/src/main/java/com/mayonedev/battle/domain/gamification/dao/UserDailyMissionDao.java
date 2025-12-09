package com.mayonedev.battle.domain.gamification.dao;

import com.mayonedev.battle.domain.gamification.entity.UserDailyMission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface UserDailyMissionDao {
    List<UserDailyMission> findByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    int insert(UserDailyMission userDailyMission);

    int updateProgress(UserDailyMission userDailyMission);

    UserDailyMission findByCompositeKey(@Param("userId") Long userId, @Param("date") LocalDate date,
            @Param("missionId") Integer missionId);

    int updateRewardStatus(@Param("userId") Long userId,
            @Param("missionDate") LocalDate missionDate,
            @Param("missionId") Integer missionId,
            @Param("isRewarded") Boolean isRewarded);
}
