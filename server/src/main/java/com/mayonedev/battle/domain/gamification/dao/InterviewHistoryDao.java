package com.mayonedev.battle.domain.gamification.dao;

import com.mayonedev.battle.domain.gamification.entity.InterviewHistory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InterviewHistoryDao {
    void insert(InterviewHistory interviewHistory);
}
