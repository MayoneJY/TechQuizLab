package com.mayonedev.battle.dao;

import com.mayonedev.battle.entity.UserQuestionHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UserQuestionHistoryDao {
    List<UserQuestionHistory> findByUserId(Long userId);
    int insert(UserQuestionHistory history);
    int countByUserIdAndCorrect(@Param("userId") Long userId, @Param("isCorrect") Boolean isCorrect);
}
