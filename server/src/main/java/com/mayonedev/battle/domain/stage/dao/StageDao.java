package com.mayonedev.battle.domain.stage.dao;

import com.mayonedev.battle.domain.stage.entity.Stage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Optional;

@Mapper
public interface StageDao {
    List<Stage> findAll();

    Optional<Stage> findById(@Param("stageId") Long stageId);
}
