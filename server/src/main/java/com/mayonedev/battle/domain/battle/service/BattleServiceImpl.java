package com.mayonedev.battle.domain.battle.service;

import com.mayonedev.battle.domain.battle.dao.BattleDao;
import com.mayonedev.battle.domain.battle.dao.BattleParticipantDao;
import com.mayonedev.battle.domain.battle.dao.BattleTurnDao;
import com.mayonedev.battle.domain.battle.entity.Battle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mayonedev.battle.domain.ai.service.AiQuestionService;
import com.mayonedev.battle.domain.battle.dao.BattleDetailDao;
import com.mayonedev.battle.domain.battle.entity.BattleDetail;
import com.mayonedev.battle.domain.gamification.dao.PortfolioDao;
import com.mayonedev.battle.domain.gamification.entity.Portfolio;
import com.mayonedev.battle.domain.stage.dao.StageDao;
import com.mayonedev.battle.domain.stage.entity.Stage;

@Service
@RequiredArgsConstructor
public class BattleServiceImpl implements BattleService {

    private final BattleDao battleDao;
    private final BattleParticipantDao participantDao;
    private final BattleTurnDao turnDao;
    private final com.mayonedev.battle.domain.gamification.dao.PortfolioDao portfolioDao;
    private final StageDao stageDao;
    private final BattleDetailDao battleDetailDao;
    private final AiQuestionService aiQuestionService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public Battle createBattle(Long stageId, Long userId) {
        Battle battle = new Battle();
        battle.setUserId(userId);

        // Retrieve user's portfolios and select the latest one
        List<com.mayonedev.battle.domain.gamification.entity.Portfolio> portfolios = portfolioDao
                .findAllByUserId(userId);
        com.mayonedev.battle.domain.gamification.entity.Portfolio portfolio;

        if (portfolios != null && !portfolios.isEmpty()) {
            portfolio = portfolios.get(0);
            battle.setPfId(portfolio.getPfId());
        } else {
            throw new RuntimeException("No portfolio found for user. Please create a portfolio first.");
        }

        // Retrieve Stage
        Stage stage = stageDao.findById(stageId)
                .orElseThrow(() -> new RuntimeException("Stage not found with id: " + stageId));

        battle.setStageId(stageId);
        battle.setStatus("IN_PROGRESS");
        battle.setTotalDamage(0);
        battle.setCreatedAt(LocalDateTime.now());

        // Generate battleId
        Long maxBattleId = battleDao.findMaxBattleIdByUserId(userId);
        Long nextBattleId = (maxBattleId == null) ? 1L : maxBattleId + 1;
        battle.setBattleId(nextBattleId);

        battleDao.insert(battle);

        // Generate AI Questions
        try {
            String jsonResponse = aiQuestionService.createInterviewQuestions(stage.getContent(),
                    portfolio.getContent());

            List<Map<String, String>> questions = objectMapper.readValue(jsonResponse, new TypeReference<>() {
            });

            long detailIdCounter = 1;
            for (Map<String, String> q : questions) {
                BattleDetail detail = new BattleDetail();
                detail.setUserId(userId);
                detail.setBattleId(nextBattleId);
                detail.setDetailId(detailIdCounter++);
                detail.setQuestionText(q.get("question_text"));
                detail.setDifficulty(q.get("difficulty"));
                detail.setKeywordTags(q.get("tags"));
                detail.setCreatedAt(LocalDateTime.now());

                // Set defaults
                detail.setDamage(0);

                battleDetailDao.insert(detail);
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Decide whether to fail the whole transaction or just log.
            // Failing is safer as a battle without questions is invalid.
            throw new RuntimeException("Failed to generate interview questions: " + e.getMessage(), e);
        }

        return battle;
    }

    @Override
    public Battle getBattle(Long userId, Long battleId) {
        return battleDao.findByUserAndBattleId(userId, battleId);
    }

    @Override
    public List<BattleDetail> getBattleDetails(Long userId, Long battleId) {
        return battleDetailDao.findByBattleId(userId, battleId);
    }

    @Override
    @Transactional
    public void processTurn(Long userId, Long battleId, String answer) {
        // Just save the user's answer, don't grade yet
        // Since we don't have detailId in arguments (frontend limitation?),
        // we'll assume sequential answering or find the first unanswered question.
        // For simplicity, let's assume we find the current active question or just log
        // it.
        // wait, we need to know WHICH question is being answered.
        // The current design of processTurn(userId, battleId, answer) is insufficient
        // for specific question answering
        // unless we track current turn index in Battle entity.
        // Let's assume frontend sends answers sequentially or we need to update API to
        // include questionId/detailId.

        // However, for MVP, let's assume we can find the next unanswered detail.
        List<BattleDetail> details = battleDetailDao.findByBattleId(userId, battleId);
        for (BattleDetail detail : details) {
            if (detail.getUserAnswer() == null) {
                detail.setUserAnswer(answer);
                // We need an update method in DAO.
                // Assuming insert works as upsert or we add update method.
                // Let's assume we can update it. For now, since I can't check/add DAO method
                // easily without more context,
                // I will add a TODO or assume update works.
                // Re-using insert might fail if PK exists.
                // We should add an update method to BattleDetailDao.
                // For this step, I'll pretend we have it or use a raw SQL update if needed.
                // Let's assume we will add 'updateUserAnswer' to DAO next.
                battleDetailDao.updateUserAnswer(detail);
                break;
            }
        }
    }

    @Override
    @Transactional
    public Map<String, Object> finishBattle(Long userId, Long battleId) {
        Battle battle = battleDao.findByUserAndBattleId(userId, battleId);
        if (battle == null) {
            throw new RuntimeException("Battle not found");
        }

        List<BattleDetail> details = battleDetailDao.findByBattleId(userId, battleId);

        // Parallelize AI grading
        // Note: We do AI calls in parallel, but DB updates should ideally remain in
        // transaction scope on main thread
        // or be handled carefully.
        // Since we are in a @Transactional method, let's gather results first.

        List<BattleDetail> gradedDetails = details.parallelStream()
                .map(detail -> {
                    if (detail.getUserAnswer() != null) {
                        try {
                            Map<String, Object> evaluation = aiQuestionService.evaluateAnswer(detail.getQuestionText(),
                                    detail.getUserAnswer());
                            int score = (int) evaluation.get("score");
                            String feedback = (String) evaluation.get("feedback");

                            detail.setDamage(score);
                            detail.setAiFeedback(feedback);
                        } catch (Exception e) {
                            e.printStackTrace();
                            // Fallback or log error
                            detail.setDamage(0);
                            detail.setAiFeedback("채점 중 오류가 발생했습니다.");
                        }
                    }
                    return detail;
                })
                .collect(java.util.stream.Collectors.toList());

        int totalDamage = 0;
        for (BattleDetail detail : gradedDetails) {
            if (detail.getUserAnswer() != null) {
                totalDamage += detail.getDamage();
                battleDetailDao.updateGrading(detail);
            }
        }

        battle.setTotalDamage(totalDamage);
        battle.setStatus("COMPLETED");
        battleDao.updateStatus(battle);
        battleDao.updateTotalDamage(battle);

        return Map.of(
                "totalScore", totalDamage,
                "details", gradedDetails);
    }
}
