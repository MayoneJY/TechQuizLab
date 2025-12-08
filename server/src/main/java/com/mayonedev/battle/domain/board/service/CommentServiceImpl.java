package com.mayonedev.battle.domain.board.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mayonedev.battle.domain.board.dao.BoardCommentDao;
import com.mayonedev.battle.domain.board.dto.CommentDto;
import com.mayonedev.battle.domain.board.entity.Comment;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private BoardCommentDao boardCommentDao;

    @Override
    @Transactional
    public void createComment(Comment comment) throws Exception {
        if (comment.getIs_deleted() == null) {
            comment.setIs_deleted(0);
        }

        boardCommentDao.insertComment(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) throws Exception {
        boardCommentDao.softDeleteComment(commentId);
    }

    @Override
    public List<CommentDto> getCommentsByPost(Integer boardId, Long postId) throws Exception {
        return boardCommentDao.selectCommentsByPost(boardId, postId);
    }

    @Override
    public Comment selectByCommentId(Integer boardId, Long postId, Long commentId) throws Exception {
        return boardCommentDao.selectByCommentId(boardId, postId, commentId);
    }
}
