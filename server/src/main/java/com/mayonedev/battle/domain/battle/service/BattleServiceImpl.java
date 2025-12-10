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
import com.mayonedev.battle.domain.battle.dao.BattleBookmarkDao;
import com.mayonedev.battle.domain.battle.dao.BattleDetailDao;
import com.mayonedev.battle.domain.battle.entity.BattleBookmark;
import com.mayonedev.battle.domain.battle.entity.BattleDetail;
import com.mayonedev.battle.domain.stage.dao.StageDao;
import com.mayonedev.battle.domain.stage.entity.Stage;

@Service
@RequiredArgsConstructor
public class BattleServiceImpl implements BattleService {

    private final com.mayonedev.battle.domain.user.dao.UserDao userDao; // Injected
    private final BattleDao battleDao;
    private final BattleParticipantDao participantDao;
    private final BattleTurnDao turnDao;
    // Removed unused PortfolioDao
    private final com.mayonedev.battle.domain.gamification.dao.PortfolioDao portfolioDao;
    private final StageDao stageDao;
    private final BattleDetailDao battleDetailDao;
    private final BattleBookmarkDao battleBookmarkDao;
    private final AiQuestionService aiQuestionService;
    private final ObjectMapper objectMapper;
    private final com.mayonedev.battle.domain.user.service.UserService userService; // Use Service to ensure consistency
                                                                                    // if needed, or Dao direct update

    @Override
    @Transactional
    public Battle createBattle(Long stageId, Long userId) {
        // [Life Check]
        com.mayonedev.battle.domain.user.entity.User user = userDao.findById(userId);
        // Ensure reset logic runs if it hasn't (though usually handled at login) - safe
        // double check or rely on login
        // But to be safe and atomic:
        if (user.getRemainingLives() == null)
            user.setRemainingLives(0);

        // Reset check (Optional here if guaranteed by filter/login, but good for
        // safety)
        if (user.getLastLivesResetAt() == null
                || !user.getLastLivesResetAt().toLocalDate().isEqual(java.time.LocalDate.now())) {
            user.setRemainingLives(5);
            user.setLastLivesResetAt(LocalDateTime.now());
            userDao.update(user);
        }

        if (user.getRemainingLives() <= 0) {
            throw new RuntimeException("오늘의 도전 횟수를 모두 소진했습니다. 내일 다시 도전해주세요!");
        }

        // Consume Life
        user.setRemainingLives(user.getRemainingLives() - 1);
        userDao.update(user);

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
        List<BattleDetail> details = battleDetailDao.findByBattleId(userId, battleId);
        for (BattleDetail detail : details) {
            if (detail.getUserAnswer() == null) {
                detail.setUserAnswer(answer);
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

    @Override
    @Transactional
    public void bookmarkBattleDetail(Long userId, Long battleId, Long detailId, String memo) {
        BattleDetail battleDetail = battleDetailDao.findByBattleId(userId, battleId).stream()
                .filter(d -> d.getDetailId().equals(detailId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Battle detail not found"));

        BattleBookmark bookmark = new BattleBookmark();
        // Temporary ID gen
        bookmark.setBookmarkId(System.currentTimeMillis() + userId);
        bookmark.setUserId(userId);
        bookmark.setRefBattleId(battleId);
        bookmark.setRefDetailId(detailId);
        bookmark.setMemo(memo);
        bookmark.setCreatedAt(java.time.LocalDateTime.now());

        battleBookmarkDao.insert(bookmark);
    }

    @Override
    public java.util.List<Battle> getMyBattles(Long userId) {
        return battleDao.findAllByUserId(userId);
    }

    @Override
    public java.util.List<com.mayonedev.battle.domain.battle.entity.BattleBookmark> getMyBookmarks(Long userId) {
        return battleBookmarkDao.findByUserId(userId);
    }

    @Override
    @Transactional
    public Battle createPracticeBattle(Long userId) {
        List<BattleBookmark> bookmarks = battleBookmarkDao.findByUserId(userId);
        if (bookmarks == null || bookmarks.isEmpty()) {
            throw new RuntimeException("북마크된 문제가 없습니다. 오답노트를 먼저 추가해주세요.");
        }

        // Shuffle and pick up to 5
        java.util.Collections.shuffle(bookmarks);
        List<BattleBookmark> selected = bookmarks.stream()
                .limit(5)
                .collect(java.util.stream.Collectors.toList());

        Battle battle = new Battle();
        battle.setUserId(userId);
        battle.setStageId(null); // Practice mode has no real stage
        battle.setStatus("IN_PROGRESS");
        battle.setTotalDamage(0);
        battle.setCreatedAt(LocalDateTime.now());
        battle.setStageTitle("오답 복습 (Practice)"); // Virtual Title

        // Generate battleId
        Long maxBattleId = battleDao.findMaxBattleIdByUserId(userId);
        Long nextBattleId = (maxBattleId == null) ? 1L : maxBattleId + 1;
        battle.setBattleId(nextBattleId);

        battleDao.insert(battle);

        long detailIdCounter = 1;
        for (BattleBookmark b : selected) {
            BattleDetail detail = new BattleDetail();
            detail.setUserId(userId);
            detail.setBattleId(nextBattleId);
            detail.setDetailId(detailIdCounter++);
            detail.setQuestionText(b.getQuestionText());
            detail.setDifficulty(b.getDifficulty() != null ? b.getDifficulty() : "Normal");
            detail.setKeywordTags(b.getKeywordTags() != null ? b.getKeywordTags() : "Practice");
            detail.setCreatedAt(LocalDateTime.now());
            detail.setDamage(0);

            battleDetailDao.insert(detail);
        }

        return battle;
    }
}
