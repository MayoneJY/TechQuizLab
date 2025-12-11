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

    List<CommentDto> selectCommentsByPost(@Param("boardId") Integer boardId, @Param("postId") Long postId);

    Comment selectByCommentId(@Param("boardId") Integer boardId, @Param("postId") Long postId,
            @Param("commentId") Long commentId);

    void deleteComment(@Param("commentId") Long commentId);
}
