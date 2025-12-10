package com.mayonedev.battle.domain.battle.dao;

import com.mayonedev.battle.domain.battle.entity.BookmarkPractice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface BookmarkPracticeDao {
    void insert(BookmarkPractice bookmarkPractice);

    List<BookmarkPractice> findByBookmarkId(@Param("userId") Long userId, @Param("bookmarkId") Long bookmarkId);

    List<BookmarkPractice> findByPracticeId(@Param("userId") Long userId, @Param("practiceId") Long practiceId);

    void updateResult(BookmarkPractice bookmarkPractice);
}
