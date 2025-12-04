package com.mayonedev.battle.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.mayonedev.battle.entity.Board;

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
