package com.mayonedev.battle.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Board {
    private int userId;              //작성자 id
    private int id;                  //게시글 id
    private String title;            //게시글 제목
    private String content;          //게시글 내용
    private LocalDateTime created_at; //글 작성일
    private LocalDateTime updated_at; //글 수정일
    private int view;                 //조회수
    private String tags;               //태그

}
