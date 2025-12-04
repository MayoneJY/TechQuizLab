package com.mayonedev.battle.domain.battle.service;

import com.mayonedev.battle.domain.battle.entity.Battle;

public interface BattleService {
    Battle createBattle(Long stageId, Long userId);

    Battle getBattle(Long userId, Long battleId);

    void processTurn(Long userId, Long battleId, String answer);

    void finishBattle(Long userId, Long battleId);
}
