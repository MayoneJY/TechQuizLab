package com.mayonedev.battle.domain.battle.dao;

import com.mayonedev.battle.domain.battle.entity.BattleBookmark;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface BattleBookmarkDao {
    void insert(BattleBookmark battleBookmark);

    List<BattleBookmark> findByUserId(Long userId);
}
