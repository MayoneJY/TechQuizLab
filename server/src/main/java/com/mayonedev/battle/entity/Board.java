package com.mayonedev.battle.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Board {
    private int userId;              //user id
    private int id;                  //post id
    private String title;            //게시글 제목
    private String content;          //게시글 내용
    private LocalDateTime created_at; //작성 시간
    private LocalDateTime updated_at; //수정 시간
    private int view;                 //조회 수
    private String tags;              //태그

}
