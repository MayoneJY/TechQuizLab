package com.mayonedev.battle.domain.battle.dao;

import com.mayonedev.battle.domain.battle.entity.BattleBookmark;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface BattleBookmarkDao {
        void insert(BattleBookmark battleBookmark);

        void update(BattleBookmark battleBookmark);

        void delete(@org.apache.ibatis.annotations.Param("userId") Long userId,
                        @org.apache.ibatis.annotations.Param("bookmarkId") Long bookmarkId);

        List<BattleBookmark> findBookmarks(
                        @org.apache.ibatis.annotations.Param("userId") Long userId,
                        @org.apache.ibatis.annotations.Param("category") String category,
                        @org.apache.ibatis.annotations.Param("search") String search,
                        @org.apache.ibatis.annotations.Param("sort") String sort,
                        @org.apache.ibatis.annotations.Param("limit") Integer limit,
                        @org.apache.ibatis.annotations.Param("offset") Integer offset);

        int countBookmarks(
                        @org.apache.ibatis.annotations.Param("userId") Long userId,
                        @org.apache.ibatis.annotations.Param("category") String category,
                        @org.apache.ibatis.annotations.Param("search") String search);

        List<String> findBookmarkCategoriesByUserId(@org.apache.ibatis.annotations.Param("userId") Long userId);

        BattleBookmark findBookmarkById(@org.apache.ibatis.annotations.Param("userId") Long userId,
                        @org.apache.ibatis.annotations.Param("bookmarkId") Long bookmarkId);
}
