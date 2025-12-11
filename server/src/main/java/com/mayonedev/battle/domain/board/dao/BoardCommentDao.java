package com.mayonedev.battle.domain.board.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.mayonedev.battle.domain.board.dto.CommentDto;
import com.mayonedev.battle.domain.board.entity.Comment;

@Mapper
public interface BoardCommentDao {

    void insertComment(Comment comment);

    void softDeleteComment(@Param("comment_id") Long comment_id);

    List<CommentDto> selectCommentsByPost(@Param("board_id") Integer board_id, @Param("post_id") Long post_id);

    Comment selectByCommentId(@Param("board_id") Integer board_id, @Param("post_id") Long post_id, @Param("comment_id") Long comment_id);

    void deleteComment(@Param("comment_id") Long comment_id);
}
