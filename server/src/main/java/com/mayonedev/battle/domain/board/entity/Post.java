package com.mayonedev.battle.domain.board.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Post {
	private long board_id;
	private long post_id;
	private String title;
	private String content;
	private long user_id;
	private long view_count;
	private LocalDateTime created_at;

	public Post(long post_id, long board_id, String title, String content) {
		super();
		this.post_id = post_id;
		this.board_id = board_id;
		this.title = title;
		this.content = content;
	}

	public Post(long board_id, String title, String content, long user_id) {
		super();
		this.board_id = board_id;
		this.user_id = user_id;
		this.title = title;
		this.content = content;
	}

}
