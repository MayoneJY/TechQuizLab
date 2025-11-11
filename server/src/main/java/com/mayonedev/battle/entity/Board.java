package com.mayonedev.battle.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Board {
    private long userId;     //작성자 id
    private long postId;    //게시글 id
    private String title;   //게시글 제목
    private String text;    //게시글 내용
    private LocalDateTime creted_at; //글 작성일
    private LocalDateTime updated_at; //글 수정일
    private long view_count;    //조회수

}
