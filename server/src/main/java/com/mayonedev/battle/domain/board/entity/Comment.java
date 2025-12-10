package com.mayonedev.battle.domain.board.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Comment {
    private Long comment_id;
    private Integer board_id;
    private Long post_id;
    private Long user_id;
    private Long parent_comment_id;
    private String content;
    private LocalDateTime created_at;
    private Integer is_deleted;

    public Comment(Integer board_id, Long post_id, Long user_id, String content) {
        this.board_id = board_id;
        this.post_id = post_id;
        this.user_id = user_id;
        this.content = content;
        this.parent_comment_id = null;
        this.is_deleted = 0;
    }

    public Comment(Integer board_id, Long post_id, Long user_id, Long parent_comment_id, String content) {
        this.board_id = board_id;
        this.post_id = post_id;
        this.user_id = user_id;
        this.parent_comment_id = parent_comment_id;
        this.content = content;
        this.is_deleted = 0;
    }
}
