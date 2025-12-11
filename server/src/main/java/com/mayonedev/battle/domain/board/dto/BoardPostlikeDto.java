package com.mayonedev.battle.domain.board.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BoardPostlikeDto {
	private int boardId;
	private long postId;
	private long userId;
	private LocalDateTime createdAt;
	private int likeCount;
	private boolean isLiked;

	public BoardPostlikeDto(int boardId, long postId, long userId) {
		this.boardId = boardId;
		this.postId = postId;
		this.userId = userId;
	}

	public BoardPostlikeDto(int boardId, long postId) {
		this.boardId = boardId;
		this.postId = postId;
	}
}
