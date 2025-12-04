package com.mayonedev.battle.dao;

import com.mayonedev.battle.entity.BattleDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface BattleDetailDao {
    void insert(BattleDetail battleDetail);

    List<BattleDetail> findByBattleId(@Param("userId") Long userId, @Param("battleId") Long battleId);
}
