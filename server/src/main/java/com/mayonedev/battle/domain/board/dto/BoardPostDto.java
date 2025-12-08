package com.mayonedev.battle.domain.board.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BoardPostDto {
	private long board_id;
	private long post_id;
	private long user_id;
	private String title;
	private String content;
	private long view_count;
	private LocalDateTime created_at;
	
	private String nickname;

	private String tags;
	
	private Long comment_count;

	public BoardPostDto(long board_id, String title, String content) {
		super();
		this.board_id = board_id;
		this.title = title;
		this.content = content;
	}

	public BoardPostDto(long user_id, String title, String content, String tags) {
		this.user_id = user_id;
		this.title = title;
		this.content = content;
		this.tags = tags;
	}

}
