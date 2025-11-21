package com.mayonedev.battle.dao;

import com.mayonedev.battle.entity.Stage;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface StageDao {
    List<Stage> findAll();
    Stage findById(Long id);
    int insert(Stage stage);
}
