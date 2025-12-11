package com.mayonedev.battle.domain.board.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Board {
	private long board_id;
	private String board_name;
	private String description;

}
