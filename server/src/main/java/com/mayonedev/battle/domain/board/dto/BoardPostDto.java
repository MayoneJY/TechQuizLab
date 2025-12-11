package com.mayonedev.battle.domain.board.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BoardPostDto {
	private long boardId;
	private long postId;
	private long userId;
	private String title;
	private String content;
	private long viewCount;
	private LocalDateTime createdAt;

	private String nickname;

	private String tags;

	private Long commentCount;

	public BoardPostDto(long boardId, String title, String content) {
		super();
		this.boardId = boardId;
		this.title = title;
		this.content = content;
	}

	public BoardPostDto(long userId, String title, String content, String tags) {
		this.userId = userId;
		this.title = title;
		this.content = content;
		this.tags = tags;
	}

}
