package com.mayonedev.battle.domain.battle.dao;

import com.mayonedev.battle.domain.battle.entity.Battle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BattleDao {
        Battle findByUserAndBattleId(@Param("userId") Long userId, @Param("battleId") Long battleId);

        void insert(Battle battle);

        void updateStatus(Battle battle);

        Long findMaxBattleIdByUserId(@Param("userId") Long userId);

        void updateTotalDamage(Battle battle);

        java.util.List<Battle> findBattles(
                        @Param("userId") Long userId,
                        @Param("category") String category,
                        @Param("search") String search,
                        @Param("sort") String sort,
                        @Param("limit") Integer limit,
                        @Param("offset") Integer offset);

        int countBattles(
                        @Param("userId") Long userId,
                        @Param("category") String category,
                        @Param("search") String search);

        java.util.List<String> findBattleCategoriesByUserId(@Param("userId") Long userId);

        void delete(@Param("userId") Long userId, @Param("battleId") Long battleId);
}
