package com.mayonedev.battle.service;

import com.mayonedev.battle.dao.BattleDao;
import com.mayonedev.battle.dao.BattleParticipantDao;
import com.mayonedev.battle.dao.BattleTurnDao;
import com.mayonedev.battle.dao.QuestionDao;
import com.mayonedev.battle.entity.Battle;
import com.mayonedev.battle.entity.BattleParticipant;
import com.mayonedev.battle.entity.BattleTurn;
import com.mayonedev.battle.entity.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BattleServiceImpl implements BattleService {

    private final BattleDao battleDao;
    private final BattleParticipantDao participantDao;
    private final BattleTurnDao turnDao;
    private final QuestionDao questionDao;

    @Override
    @Transactional
    public Battle createBattle(Long stageId, String mode, Integer difficulty, Long userId) {
        Battle battle = new Battle();
        battle.setStageId(stageId);
        battle.setMode(mode);
        battle.setStatus("READY");
        battle.setDifficulty(difficulty);
        battle.setCreatedAt(LocalDateTime.now());
        battleDao.insert(battle);

        BattleParticipant participant = new BattleParticipant();
        participant.setBattleId(battle.getId());
        participant.setUserId(userId);
        participant.setRole("LEADER");
        participant.setScore(0);
        participant.setJoinedAt(LocalDateTime.now());
        participantDao.insert(participant);

        return battle;
    }

    @Override
    public Battle getBattle(Long battleId) {
        return battleDao.findById(battleId);
    }

    @Override
    @Transactional
    public BattleTurn processTurn(Long battleId, Long userId, String answer) {
        // Simplified logic: assume question is already known or passed (in real app, need to track current question)
        // For now, we just record the turn.
        
        BattleTurn turn = new BattleTurn();
        turn.setBattleId(battleId);
        turn.setTurnNo(1); // Need to calculate next turn no
        turn.setAttackerId(userId);
        turn.setAnswerText(answer);
        turn.setMultiplier(1.0f);
        turn.setDamage(100); // Dummy damage
        turn.setCreatedAt(LocalDateTime.now());
        
        turnDao.insert(turn);
        
        return turn;
    }

    @Override
    @Transactional
    public void finishBattle(Long battleId) {
        Battle battle = battleDao.findById(battleId);
        if (battle != null) {
            battle.setStatus("FINISHED");
            battle.setClosedAt(LocalDateTime.now());
            battleDao.updateStatus(battle);
        }
    }
}
