package com.mayonedev.battle.service;

import com.mayonedev.battle.dao.TopicDao;
import com.mayonedev.battle.dao.UserTopicLevelDao;
import com.mayonedev.battle.entity.Topic;
import com.mayonedev.battle.entity.UserTopicLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {

    private final TopicDao topicDao;
    private final UserTopicLevelDao userTopicLevelDao;

    @Override
    public List<Topic> getAllTopics() {
        return topicDao.findAll();
    }

    @Override
    public Topic getTopicById(Long id) {
        return topicDao.findById(id);
    }

    @Override
    public UserTopicLevel getUserTopicLevel(Long userId, Long topicId) {
        UserTopicLevel level = userTopicLevelDao.findByUserIdAndTopicId(userId, topicId);
        if (level == null) {
            // Create default level if not exists
            level = new UserTopicLevel();
            level.setUserId(userId);
            level.setTopicId(topicId);
            level.setLevel(1);
            level.setExp(0);
            userTopicLevelDao.insert(level);
        }
        return level;
    }
}
