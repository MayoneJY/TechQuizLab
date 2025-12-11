package com.mayonedev.battle.domain.gamification.dao;

import com.mayonedev.battle.domain.gamification.entity.DailyMission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface DailyMissionDao {
    List<DailyMission> findAll();

    DailyMission findById(@Param("missionId") Integer missionId);
}
