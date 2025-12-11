package com.mayonedev.battle.domain.board.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.mayonedev.battle.domain.board.dto.CommentDto;
import com.mayonedev.battle.domain.board.entity.Comment;

@Mapper
public interface BoardCommentDao {

    void insertComment(Comment comment);

    void softDeleteComment(@Param("commentId") Long commentId);

    // Using int for boardId to match usage in selectCommentsByPost, though long is
    // preferred.
    // MyBatis usually handles type conversion, but consistency helps.
    List<CommentDto> selectCommentsByPost(@Param("boardId") Integer boardId, @Param("postId") Long postId);

    Comment selectByCommentId(@Param("boardId") Integer boardId, @Param("postId") Long postId,
            @Param("commentId") Long commentId);

    void deleteComment(@Param("commentId") Long commentId);

    // Updated to long to be safe, assuming params are passed as long.
    void disconnectComments(@Param("boardId") long boardId, @Param("postId") long postId);

    void deleteCommentsByPostId(@Param("boardId") long boardId, @Param("postId") long postId);
}
