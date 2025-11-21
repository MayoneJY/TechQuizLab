package com.mayonedev.battle.dao;

import com.mayonedev.battle.entity.BattleParticipant;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface BattleParticipantDao {
    List<BattleParticipant> findByBattleId(Long battleId);
    int insert(BattleParticipant participant);
    int updateScore(BattleParticipant participant);
}
