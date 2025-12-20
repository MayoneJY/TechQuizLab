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
    private final com.mayonedev.battle.domain.battle.dao.BookmarkPracticeDao bookmarkPracticeDao;
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
        battle.setStatus("READY");
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
        Battle battle = battleDao.findByUserAndBattleId(userId, battleId);
        if (battle != null && battle.getStageId() != null && battle.getStageId() == 0) {
            List<com.mayonedev.battle.domain.battle.entity.BookmarkPractice> practices = bookmarkPracticeDao
                    .findByPracticeId(userId, battleId);
            return practices.stream().map(p -> {
                BattleDetail d = new BattleDetail();
                d.setUserId(p.getUserId());
                d.setBattleId(p.getPracticeId());
                d.setDetailId(p.getBookmarkId()); // Use bookmarkId as detailId or just map it
                // Actually DetailId in BattleDetail is usually sequential 1..N.
                // Here we can use appropriate mapping.
                // Let's use bookmarkId as ID for now since we don't have sequential detail_id
                // in bookmark_practice without extra logic.
                d.setQuestionText(p.getQuestionText());
                d.setDifficulty(p.getDifficulty());
                d.setKeywordTags(p.getKeywordTags());
                d.setUserAnswer(p.getUserAnswer());
                d.setAiFeedback(p.getAiFeedback());
                d.setDamage(p.getDamage());
                d.setCreatedAt(p.getCreatedAt());
                return d;
            }).collect(java.util.stream.Collectors.toList());
        }
        return battleDetailDao.findByBattleId(userId, battleId);
    }

    @Override
    @Transactional
    public void processTurn(Long userId, Long battleId, String answer) {
        Battle battle = battleDao.findByUserAndBattleId(userId, battleId);
        if (battle != null && battle.getStageId() != null && battle.getStageId() == 0) {
            List<com.mayonedev.battle.domain.battle.entity.BookmarkPractice> practices = bookmarkPracticeDao
                    .findByPracticeId(userId, battleId);
            // Find first unanswered
            // Note: findByPracticeId might sort by bookmarkId or practiceId sequence.
            // We need consistent order. The mapper sorts by practice_id (which is
            // battle_id) which is constant?
            // Ah, BookmarkPractice doesn't store 'order'. We rely on `findByPracticeId`
            // return order.
            // We should find the first one where userAnswer is null.
            for (com.mayonedev.battle.domain.battle.entity.BookmarkPractice p : practices) {
                if (p.getUserAnswer() == null) {
                    p.setUserAnswer(answer);
                    bookmarkPracticeDao.updateResult(p);
                    break;
                }
            }
            return;
        }

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

        if (battle.getStageId() != null && battle.getStageId() == 0) {
            List<com.mayonedev.battle.domain.battle.entity.BookmarkPractice> practices = bookmarkPracticeDao
                    .findByPracticeId(userId, battleId);

            List<com.mayonedev.battle.domain.battle.entity.BookmarkPractice> gradedPractices = practices
                    .parallelStream()
                    .map(p -> {
                        if (p.getUserAnswer() != null) {
                            try {
                                Map<String, Object> evaluation = aiQuestionService.evaluateAnswer(p.getQuestionText(),
                                        p.getUserAnswer());
                                int score = (int) evaluation.get("score");
                                String feedback = (String) evaluation.get("feedback");

                                p.setDamage(score);
                                p.setAiFeedback(feedback);
                            } catch (Exception e) {
                                e.printStackTrace();
                                p.setDamage(0);
                                p.setAiFeedback("채점 중 오류가 발생했습니다.");
                            }
                        }
                        return p;
                    }).collect(java.util.stream.Collectors.toList());

            int totalDamage = 0;
            for (com.mayonedev.battle.domain.battle.entity.BookmarkPractice p : gradedPractices) {
                if (p.getUserAnswer() != null) {
                    totalDamage += (p.getDamage() == null ? 0 : p.getDamage());
                    bookmarkPracticeDao.updateResult(p);
                }
            }

            battle.setTotalDamage(totalDamage);
            battle.setStatus("COMPLETED");
            battleDao.updateStatus(battle);
            battleDao.updateTotalDamage(battle);

            // Convert to format expected by frontend (BattleDetail list?)
            // finishBattle returns Map with "details"
            List<BattleDetail> details = gradedPractices.stream().map(p -> {
                BattleDetail d = new BattleDetail();
                d.setUserId(p.getUserId());
                d.setBattleId(p.getPracticeId());
                d.setDetailId(p.getBookmarkId());
                d.setQuestionText(p.getQuestionText());
                d.setDifficulty(p.getDifficulty());
                d.setKeywordTags(p.getKeywordTags());
                d.setUserAnswer(p.getUserAnswer());
                d.setAiFeedback(p.getAiFeedback());
                d.setDamage(p.getDamage());
                d.setCreatedAt(p.getCreatedAt());
                return d;
            }).collect(java.util.stream.Collectors.toList());

            return Map.of(
                    "totalScore", totalDamage,
                    "details", details);
        }

        List<BattleDetail> details = battleDetailDao.findByBattleId(userId, battleId);

        // ... Normal logic ...

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
    public java.util.Map<String, Object> getMyBattles(Long userId, String category, String search, String sort,
            int page, int size) {
        int offset = (page - 1) * size;
        java.util.List<Battle> battles = battleDao.findBattles(userId, category, search, sort, size, offset);
        int totalElements = battleDao.countBattles(userId, category, search);
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return Map.of(
                "content", battles,
                "totalPages", totalPages,
                "totalElements", totalElements,
                "currentPage", page);
    }

    @Override
    public java.util.Map<String, Object> getMyBookmarks(Long userId, String category, String search, String sort,
            int page, int size) {
        int offset = (page - 1) * size;
        java.util.List<BattleBookmark> bookmarks = battleBookmarkDao.findBookmarks(userId, category, search, sort, size,
                offset);
        int totalElements = battleBookmarkDao.countBookmarks(userId, category, search);
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return Map.of(
                "content", bookmarks,
                "totalPages", totalPages,
                "totalElements", totalElements,
                "currentPage", page);
    }

    @Override
    public java.util.List<String> getBookmarkCategories(Long userId) {
        return battleBookmarkDao.findBookmarkCategoriesByUserId(userId);
    }

    @Override
    public BattleBookmark getBookmark(Long userId, Long bookmarkId) {
        BattleBookmark bookmark = battleBookmarkDao.findBookmarkById(userId, bookmarkId);
        if (bookmark == null) {
            throw new RuntimeException("Bookmark not found");
        }
        return bookmark;
    }

    @Override
    @Transactional
    public void updateBookmark(Long userId, Long bookmarkId, String memo) {
        BattleBookmark bookmark = battleBookmarkDao.findBookmarkById(userId, bookmarkId);
        if (bookmark == null) {
            throw new RuntimeException("Bookmark not found");
        }
        bookmark.setMemo(memo);
        battleBookmarkDao.update(bookmark);
    }

    @Override
    @Transactional
    public void deleteBookmark(Long userId, Long bookmarkId) {
        battleBookmarkDao.delete(userId, bookmarkId);
    }

    @Override
    @Transactional
    public Battle createPracticeBattle(Long userId) {
        List<BattleBookmark> bookmarks = battleBookmarkDao.findBookmarks(userId, null, null, null, null, null);
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

        // Retrieve user's portfolios and select the latest one
        List<com.mayonedev.battle.domain.gamification.entity.Portfolio> portfolios = portfolioDao
                .findAllByUserId(userId);
        if (portfolios != null && !portfolios.isEmpty()) {
            battle.setPfId(portfolios.get(0).getPfId());
        } else {
            // For practice, if they have no portfolio but have bookmarks (unlikely but
            // possible if portfolio deleted?),
            // we might need a fallback or fail.
            // Given bookmarks exist, they probably had a portfolio.
            // If portfolio is missing, maybe set 0 or fail. Let's fail for consistency.
            throw new RuntimeException("Portfolio not found. Please create a portfolio first.");
        }

        battle.setStageId(0L); // Practice mode uses 0
        battle.setStatus("IN_PROGRESS");
        battle.setTotalDamage(0);
        battle.setCreatedAt(LocalDateTime.now());
        battle.setStageTitle("오답 복습 (Practice)");

        // Generate battleId
        Long maxBattleId = battleDao.findMaxBattleIdByUserId(userId);
        Long nextBattleId = (maxBattleId == null) ? 1L : maxBattleId + 1;
        battle.setBattleId(nextBattleId);

        battleDao.insert(battle);

        for (BattleBookmark b : selected) {
            com.mayonedev.battle.domain.battle.entity.BookmarkPractice practice = new com.mayonedev.battle.domain.battle.entity.BookmarkPractice();
            practice.setUserId(userId);
            practice.setBookmarkId(b.getBookmarkId());
            practice.setPracticeId(nextBattleId); // Use BattleId as PracticeId
            practice.setCreatedAt(LocalDateTime.now());
            practice.setDamage(0);

            bookmarkPracticeDao.insert(practice);
        }

        return battle;
    }

    @Override
    @Transactional
    public void deleteBattle(Long userId, Long battleId) {
        // Verify ownership
        Battle battle = battleDao.findByUserAndBattleId(userId, battleId);
        if (battle == null) {
            throw new RuntimeException("Battle not found or access denied");
        }

        // Delete related data first (FK constraints? usually CASCADE but let's be safe
        // or rely on DB)
        // Check cascade rules. If unclear, manual delete.
        // Assuming Mybatis and simple tables, we might need to delete details first.

        // Actually, let's check Dao structure. We have battle_detail, battle_turn, etc.
        // For simplicity, if DB has cascade, `battleDao.delete` is enough.
        // If not, we need `battleDetailDao.deleteByBattleId` etc.
        // Let's assume we need to delete details.

        bookmarkPracticeDao.deleteByPracticeId(userId, battleId);
        battleDetailDao.deleteByBattleId(userId, battleId);
        // turnDao?
        // bookmark? (bookmarks are valuable, maybe keep? but they reference battle.
        // If battle is deleted, bookmark references might break if FK.
        // But usually bookmarks are for *questions* (details).
        // If we delete battle, we delete details. So bookmarks pointing to details
        // might break.
        // However, user said "Give Up" -> "Delete Battle".
        // Usually "Give Up" means "Cancel this attempt".
        // If user actively bookmarked something during this failed attempt, keeping it
        // might be good,
        // but technical constraints might force deletion.
        // Let's delete for now as per "Delete Battle".

        battleDao.delete(userId, battleId);
    }

    @Override
    public java.util.List<String> getBattleCategories(Long userId) {
        return battleDao.findBattleCategoriesByUserId(userId);
    }
}
