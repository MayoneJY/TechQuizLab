package com.mayonedev.battle.dao;

import com.mayonedev.battle.entity.Topic;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface TopicDao {
    List<Topic> findAll();
    Topic findById(Long id);
    int insert(Topic topic);
}
