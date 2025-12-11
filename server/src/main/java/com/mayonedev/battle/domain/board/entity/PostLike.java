package com.mayonedev.battle.domain.board.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostLike {
	private int board_id;
	private long post_id;
	private long user_id;
	private LocalDateTime created_at;

	public PostLike(int board_id, long post_id, long user_id) {
		this.board_id = board_id;
		this.post_id = post_id;
		this.user_id = user_id;
	}
}

