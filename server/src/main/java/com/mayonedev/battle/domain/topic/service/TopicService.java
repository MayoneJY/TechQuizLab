package com.mayonedev.battle.domain.topic.service;

import com.mayonedev.battle.domain.topic.entity.Topic;
import com.mayonedev.battle.domain.topic.entity.UserTopicLevel;
import java.util.List;

public interface TopicService {
    List<Topic> getAllTopics();

    Topic getTopicById(Long id);

    UserTopicLevel getUserTopicLevel(Long userId, Long topicId);
}
