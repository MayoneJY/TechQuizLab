package com.mayonedev.battle.domain.board.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BoardPostDto {

    private String title;
    private String text;
    private Long id;
    private String nickname;
    private Long viewCount;
    private LocalDateTime createAt;
    private LocalDateTime updatedAt;

}
