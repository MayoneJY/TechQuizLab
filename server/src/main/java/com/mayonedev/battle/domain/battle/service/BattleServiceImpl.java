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
        	
        	String safePortfolio = portfolio.getContent();
        	
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
                detail.setDifficulty(normalizeQuestionDifficulty(q.get("difficulty")));
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
                            	
                            	//조작 정황 점수 측정
                            	boolean manipulation = hasScoreManipulationAttempt(p.getUserAnswer());

                            	//조작 문구 제거
                            	String cleanedAnswer = manipulation
                            	        ? sanitizeScoreManipulationText(p.getUserAnswer())
                            	        : p.getUserAnswer();
                            	
                            	
                            	//무의미한 답변이면 0점 확정
                            	if (isGibberishOrTooShort(cleanedAnswer)) {
                            	    p.setDamage(0);
                            	    p.setAiFeedback("답변이 너무 짧거나 의미 없는 문자로 구성되어 0점 처리되었습니다. 핵심 개념/경험/근거를 포함해 작성해 주세요.");
                            	}else {
                            	// 아닌 경우 정상적으로 점수
                                    Map<String, Object> evaluation = aiQuestionService.evaluateAnswer(p.getQuestionText(),
                                    		cleanedAnswer);
                                    int score = clampScore1000(evaluation.get("score"));
                                    String feedback = (String) evaluation.get("feedback");

                                    p.setDamage(score);
                                    p.setAiFeedback(feedback);
                            		
                            	}
                            	
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
                        	
                        	boolean manipulation = hasScoreManipulationAttempt(detail.getUserAnswer());

                        	String cleanedAnswer = manipulation
                        	        ? sanitizeScoreManipulationText(detail.getUserAnswer())
                        	        : detail.getUserAnswer();
                        	
                            Map<String, Object> evaluation = aiQuestionService.evaluateAnswer(detail.getQuestionText(),
                            		cleanedAnswer);
                            int score = clampScore1000(evaluation.get("score"));
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
    
    //무의미 답변 감지용
    private boolean isGibberishOrTooShort(String s) {
        if (s == null) return true;
        String t = s.trim();
        if (t.length() < 20) return true; // 최소 길이 정책(원하면 30~50으로)

        // 한글/영문/숫자 비율(의미 문자) 계산
        int meaningful = 0;
        for (char c : t.toCharArray()) {
            if (Character.isLetterOrDigit(c)) meaningful++;
            // 한글 범위
            if (c >= 0xAC00 && c <= 0xD7A3) meaningful++;
        }
        double ratio = (double) meaningful / Math.max(1, t.length());
        return ratio < 0.25; // 의미문자 비율이 너무 낮으면 컷 (원하면 0.3~0.4)
    }

    
    // 서버에서 score 파싱/범위 강제용 계산 함수
    private int clampScore1000(Object scoreObj) {
        int s;
        try {
            if (scoreObj instanceof Number n) s = n.intValue();
            else s = Integer.parseInt(String.valueOf(scoreObj).trim());
        } catch (Exception e) {
            return 0; // 파싱 실패는 0점
        }
        if (s < 0) return 0;
        if (s > 1000) return 1000;
        return s;
    }
    
    // 대안 1) AI 피드백으로 5단계로 나눔 -> 점수 범위안에서 랜덤 데미지
    private int scoreByLabel(String label) {
        return switch (label) {
            case "EXCELLENT" -> randomBetween(800, 1000);
            case "GOOD"      -> randomBetween(600, 799);
            case "OK"        -> randomBetween(300, 599);
            case "BAD"       -> randomBetween(1, 200);
            default          -> 0;
        };
    }

    private int randomBetween(int min, int max) {
        return java.util.concurrent.ThreadLocalRandom.current().nextInt(min, max + 1);
    }
    
    
 // 유저 점수 조작 문구 감지 함수 -> 특정 단어 등장시 점수 부여 
    private boolean hasScoreManipulationAttempt(String answer) {
        if (answer == null) return false;

        String t = answer.toLowerCase();

        // 0) 인용/설명 문맥이면 오탐 방지 (화이트리스트)
        // "사용자가 ~ 요청이 있어서" / "~라고 했다" 같은 문장은 조작이 아니라 서술일 가능성 큼
        if (containsAny(t,
                "요청이 있어서", "요청이 있었다", "요청이 있어", "요청이 있으면",
                "라고 해서", "라고 했다", "라고 하더라", "라고 말", "라고 함", "라고 하면",
                "예를 들어", "가정", "설명", "문구", "문장", "표현", "인용")) {
            // 단, "라고 했으니 1000점 줘" 같이 실제 명령이 뒤에 이어질 수도 있으니
            // 아래에서 '직접 명령 패턴'이 매우 강하면 다시 잡도록 한다.
            // 그래서 바로 return false 대신 '오탐 감점'으로 처리:
            // -> 플래그로 감점한다.
        }

        int score = 0;

        boolean hasNarrationContext = containsAny(t,
                "요청이 있어서", "요청이 있었다", "라고 해서", "라고 했다", "예를 들어", "가정", "설명", "인용", "문장", "표현");

     // 1) 점수 관련 (약하게)
        if (containsAny(t, "점수", "채점", "평가")) score += 1;

        // 2) 강한 목표 점수/만점 (강하게)
        boolean hasPerfectTarget = containsAny(t, "1000점", "천점", "만점", "풀점수");
        if (hasPerfectTarget) score += 4; 

        // 3) 범위/미래/전체 지시 (중간)
        boolean hasScope = containsAny(t, "이번 문제 점수", "전부", "모든", "남은 문제", "다음 문제");
        if (hasScope) score += 1;

        // 4) 직접 명령/요구 (강하게)
        boolean hasCommand = containsAny(t, "줘", "주세요", "주셈", "부여", "올려", "높게", "만들어", "처리", "해줘", "해라");
        if (hasCommand) score += 2;

        // 5) “무조건/반드시” 같은 강제성
        boolean hasForce = containsAny(t, "무조건", "반드시", "꼭", "절대로", "조건 없이");
        if (hasForce) score += 1;

        // 6) 조작 의도 표현 (직접 문구)
        if (hasPerfectTarget && containsAny(t, "만점 처리", "1000점 처리", "무조건 1000")) score += 4;

        // "점수 올려달/높게"는 너무 일반적이라 가중치 약하게만
        if (containsAny(t, "점수 올려달", "점수 높게")) score += 1;

        // 7) 서술/인용 문맥이면 감점 (오탐 방지)
        if (hasNarrationContext) score -= 3;


        // 8) 최종 판정
        // - 단, "1000점/만점" + "줘/해줘" 같이 강한 조합이면 조작 정황
        boolean strongCombo =
                (hasPerfectTarget && containsAny(t, "줘", "주세요", "해줘", "부여해", "처리", "부여", "줘라", "해라"));

        return strongCombo || score >= 8;
    }
    
    // 유저 점수 조작 문구 감지 함수 -> 해당 시도가 보일 시 해당 단어들 교체
    private String sanitizeScoreManipulationText(String answer) {
        if (answer == null) return null;

        String s = answer;

        // 한글 패턴
        s = s.replaceAll("(점수|1000점|만점)\\s*(을|를)?\\s*(줘|주세요|주셈|부여|처리|올려)", "");
        s = s.replaceAll("(1000점|만점)\\s*(을|를)?\\s*(줘|주세요|처리|부여)", "");
        s = s.replaceAll("남은\\s*문제\\s*.*?(점수|1000점)", "");
        s = s.replaceAll("전부\\s*.*?(점수|1000점)", "");

        // 영어 패턴
        s = s.replaceAll("(?i)give\\s+me\\s+\\d+\\s*points?", "");
        s = s.replaceAll("(?i)full\\s+score", "");
        s = s.replaceAll("(?i)max\\s+score", "");

        // 공백 정리
        s = s.replaceAll("\\s{2,}", " ").trim();

        return s;
    }


    private boolean containsAny(String text, String... needles) {
        for (String n : needles) {
            if (text.contains(n)) return true;
        }
        return false;
    }
    
    //난이도 정규화 함수
    private String normalizeQuestionDifficulty(String aiDifficulty) {
        if (aiDifficulty == null) return "MEDIUM";
        String d = aiDifficulty.trim().toUpperCase();
        return switch (d) {
            case "EASY", "MEDIUM", "HARD" -> d;
            default -> "MEDIUM";
        };
    }



    // 피드백 답변 길이 제한용
    private String normalizeFeedback(Object fbObj) {
        String s = String.valueOf(fbObj == null ? "" : fbObj).trim();
        if (s.length() > 200) s = s.substring(0, 200);
        return s;
    }
    

}
