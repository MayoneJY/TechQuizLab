package com.mayonedev.battle.domain.board.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Comment {
    private Long commentId;
    private Integer boardId;
    private Long postId;
    private Long userId;
    private Long parentCommentId;
    private String content;
    private LocalDateTime createdAt;
    private Integer isDeleted;

    public Comment(Integer boardId, Long postId, Long userId, String content) {
        this.boardId = boardId;
        this.postId = postId;
        this.userId = userId;
        this.content = content;
        this.parentCommentId = null;
        this.isDeleted = 0;
    }

    public Comment(Integer boardId, Long postId, Long userId, Long parentCommentId, String content) {
        this.boardId = boardId;
        this.postId = postId;
        this.userId = userId;
        this.parentCommentId = parentCommentId;
        this.content = content;
        this.isDeleted = 0;
    }
}
