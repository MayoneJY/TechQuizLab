package com.mayonedev.battle.domain.topic.dao;

import com.mayonedev.battle.domain.topic.entity.UserTopicLevel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UserTopicLevelDao {
    List<UserTopicLevel> findByUserId(Long userId);

    UserTopicLevel findByUserIdAndTopicId(@Param("userId") Long userId, @Param("topicId") Long topicId);

    int insert(UserTopicLevel userTopicLevel);

    int update(UserTopicLevel userTopicLevel);
}
