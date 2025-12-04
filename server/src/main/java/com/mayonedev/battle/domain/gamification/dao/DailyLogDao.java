package com.mayonedev.battle.domain.gamification.dao;

import com.mayonedev.battle.domain.gamification.entity.DailyLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDate;

@Mapper
public interface DailyLogDao {
    void insert(DailyLog dailyLog);

    DailyLog findByUserAndDate(@Param("userId") Long userId, @Param("logDate") LocalDate logDate);

    void update(DailyLog dailyLog);
}
