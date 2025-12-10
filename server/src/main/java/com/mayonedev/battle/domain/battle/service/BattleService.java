package com.mayonedev.battle.domain.battle.service;

import com.mayonedev.battle.domain.battle.entity.Battle;

public interface BattleService {
    Battle createBattle(Long stageId, Long userId);

    Battle getBattle(Long userId, Long battleId);

    java.util.List<com.mayonedev.battle.domain.battle.entity.BattleDetail> getBattleDetails(Long userId, Long battleId);

    void processTurn(Long userId, Long battleId, String answer);

    java.util.Map<String, Object> finishBattle(Long userId, Long battleId);

    void bookmarkBattleDetail(Long userId, Long battleId, Long detailId, String memo);
}
