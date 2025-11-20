package com.mayonedev.battle.service;

import com.mayonedev.battle.entity.Topic;
import com.mayonedev.battle.entity.UserTopicLevel;
import java.util.List;

public interface TopicService {
    List<Topic> getAllTopics();
    Topic getTopicById(Long id);
    UserTopicLevel getUserTopicLevel(Long userId, Long topicId);
}
