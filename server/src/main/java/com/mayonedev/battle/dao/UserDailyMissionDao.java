package com.mayonedev.battle.dao;

import com.mayonedev.battle.entity.UserDailyMission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface UserDailyMissionDao {
    List<UserDailyMission> findByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);
    int insert(UserDailyMission userDailyMission);
    int updateProgress(UserDailyMission userDailyMission);
    int updateClaimed(Long id);
}
