package com.mayonedev.battle.domain.board.service;

import java.util.List;

import com.mayonedev.battle.domain.board.dto.CommentDto;
import com.mayonedev.battle.domain.board.entity.Comment;

public interface CommentService {

    void createComment(Comment comment) throws Exception;

    void deleteComment(Long commentId) throws Exception;

    List<CommentDto> getCommentsByPost(Integer boardId, Long postId) throws Exception;

    Comment selectByCommentId(Integer boardId, Long postId, Long commentId) throws Exception;
}
