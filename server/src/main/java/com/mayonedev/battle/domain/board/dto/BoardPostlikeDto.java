package com.mayonedev.battle.domain.board.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BoardPostlikeDto {
	private int board_id;
	private long post_id;
	private long user_id;
	private LocalDateTime created_at;
	private int like_count;
	private boolean is_liked;

	public BoardPostlikeDto(int board_id, long post_id, long user_id) {
		this.board_id = board_id;
		this.post_id = post_id;
		this.user_id = user_id;
	}

	public BoardPostlikeDto(int board_id, long post_id){
		this.board_id = board_id;
		this.post_id = post_id;
	}
}

