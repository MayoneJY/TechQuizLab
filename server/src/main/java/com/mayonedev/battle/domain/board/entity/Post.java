package com.mayonedev.battle.domain.board.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Post {
	private long boardId;
	private long postId;
	private String title;
	private String content;
	private long userId;
	private long viewCount;
	private LocalDateTime createdAt;

	public Post(long postId, long boardId, String title, String content) {
		super();
		this.postId = postId;
		this.boardId = boardId;
		this.title = title;
		this.content = content;
	}

	public Post(long boardId, String title, String content, long userId) {
		super();
		this.boardId = boardId;
		this.userId = userId;
		this.title = title;
		this.content = content;
	}

}
