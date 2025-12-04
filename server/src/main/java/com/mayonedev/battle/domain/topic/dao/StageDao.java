package com.mayonedev.battle.domain.topic.dao;

import com.mayonedev.battle.domain.topic.entity.Stage;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface StageDao {
    List<Stage> findAll();

    Stage findById(Long stageId);

    void insert(Stage stage);
}
