package com.mayonedev.battle.dao;

import com.mayonedev.battle.entity.Battle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BattleDao {
    Battle findByUserAndBattleId(@Param("userId") Long userId, @Param("battleId") Long battleId);

    void insert(Battle battle);

    void updateStatus(Battle battle);
}
