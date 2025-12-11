package com.mayonedev.battle.domain.board.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommentDto {
    private Long commentId;
    private Integer boardId;
    private Long postId;
    private Long userId;
    private Long parentCommentId;
    private String content;
    private LocalDateTime createdAt;
    private Integer isDeleted;

    private String nickname;
}
