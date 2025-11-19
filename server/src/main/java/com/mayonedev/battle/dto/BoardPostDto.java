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
    //작성시 사용
    private String title;
    private String text;

    //목록 응답시 사용 - 
    private Long id;
    private String nickname;
    private Long viewCount;
    private LocalDateTime createAt;
    private LocalDateTime updatedAt;

    //포스트맨
}
