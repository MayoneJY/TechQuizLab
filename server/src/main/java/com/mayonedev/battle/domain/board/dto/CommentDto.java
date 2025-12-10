package com.mayonedev.battle.domain.board.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommentDto {
    private Long comment_id;
    private Integer board_id;
    private Long post_id;
    private Long user_id;
    private Long parent_comment_id;
    private String content;
    private LocalDateTime created_at;
    private Integer is_deleted;

    private String nickname;
}
