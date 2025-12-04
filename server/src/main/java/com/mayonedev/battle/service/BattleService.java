package com.mayonedev.battle.service;

import com.mayonedev.battle.entity.Battle;
import com.mayonedev.battle.entity.BattleTurn;

public interface BattleService {
    Battle createBattle(Long stageId, String mode, Integer difficulty, Long userId);
    Battle getBattle(Long battleId);
    BattleTurn processTurn(Long battleId, Long userId, String answer);
    void finishBattle(Long battleId);
}
