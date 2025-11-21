package com.mayonedev.battle.dao;

import com.mayonedev.battle.entity.QuestionBookmark;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface QuestionBookmarkDao {
    List<QuestionBookmark> findByUserId(Long userId);
    int insert(QuestionBookmark bookmark);
    int deleteById(Long id);
    boolean existsByUserIdAndQuestionId(@Param("userId") Long userId, @Param("questionId") Long questionId);
}
