package com.mayonedev.battle.dao;

import com.mayonedev.battle.entity.DailyMission;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface DailyMissionDao {
    List<DailyMission> findAll();
    DailyMission findById(Long id);
}
