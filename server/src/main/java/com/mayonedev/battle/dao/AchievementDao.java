package com.mayonedev.battle.dao;

import com.mayonedev.battle.entity.Achievement;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface AchievementDao {
    List<Achievement> findAll();
    Achievement findById(Long id);
    List<Achievement> findByConditionType(String conditionType);
}
