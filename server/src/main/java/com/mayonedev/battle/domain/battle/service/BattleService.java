package com.mayonedev.battle.domain.battle.service;

import com.mayonedev.battle.domain.battle.entity.Battle;
import com.mayonedev.battle.domain.battle.entity.BattleBookmark;

public interface BattleService {
        Battle createBattle(Long stageId, Long userId);

        Battle getBattle(Long userId, Long battleId);

        java.util.List<com.mayonedev.battle.domain.battle.entity.BattleDetail> getBattleDetails(Long userId,
                        Long battleId);

        void processTurn(Long userId, Long battleId, String answer);

        java.util.Map<String, Object> finishBattle(Long userId, Long battleId);

        void deleteBattle(Long userId, Long battleId);

        void bookmarkBattleDetail(Long userId, Long battleId, Long detailId, String memo);

        java.util.Map<String, Object> getMyBattles(Long userId, String category, String search, String sort, int page,
                        int size);

        java.util.Map<String, Object> getMyBookmarks(Long userId, String category, String search, String sort, int page,
                        int size);

        Battle createPracticeBattle(Long userId);

        java.util.List<String> getBattleCategories(Long userId);

        java.util.List<String> getBookmarkCategories(Long userId);

        BattleBookmark getBookmark(Long userId, Long bookmarkId);

        void updateBookmark(Long userId, Long bookmarkId, String memo);

        void deleteBookmark(Long userId, Long bookmarkId);
}
