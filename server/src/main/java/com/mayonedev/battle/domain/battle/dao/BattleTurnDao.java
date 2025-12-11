package com.mayonedev.battle.domain.battle.dao;

import com.mayonedev.battle.domain.battle.entity.BattleTurn;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface BattleTurnDao {
    List<BattleTurn> findByBattleId(Long battleId);
    int insert(BattleTurn turn);
}
