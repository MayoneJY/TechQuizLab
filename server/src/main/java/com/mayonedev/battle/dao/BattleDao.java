package com.mayonedev.battle.dao;

import com.mayonedev.battle.entity.Battle;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BattleDao {
    Battle findById(Long id);
    int insert(Battle battle);
    int updateStatus(Battle battle);
}
