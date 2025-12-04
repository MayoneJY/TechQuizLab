package com.mayonedev.battle.dao;

import com.mayonedev.battle.entity.BookmarkPractice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface BookmarkPracticeDao {
    void insert(BookmarkPractice bookmarkPractice);

    List<BookmarkPractice> findByBookmarkId(@Param("userId") Long userId, @Param("bookmarkId") Long bookmarkId);
}
