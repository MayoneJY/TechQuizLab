package com.mayonedev.battle.domain.question.dao;

import com.mayonedev.battle.domain.question.entity.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface QuestionDao {
    List<Question> findByTopicId(Long topicId);
    Question findById(Long id);
    int insert(Question question);
    List<Question> findByTopicIdAndDifficulty(@Param("topicId") Long topicId, @Param("difficulty") Integer difficulty);
}
