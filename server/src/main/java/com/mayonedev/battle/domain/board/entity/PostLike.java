package com.mayonedev.battle.domain.board.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostLike {
	private int boardId;
	private long postId;
	private long userId;
	private LocalDateTime createdAt;

	public PostLike(int boardId, long postId, long userId) {
		this.boardId = boardId;
		this.postId = postId;
		this.userId = userId;
	}
}
