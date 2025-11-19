package com.mayonedev.battle.service;

import com.mayonedev.battle.dto.BoardPostDto;
import com.mayonedev.battle.entity.Board;
import java.util.*;

public interface BoardService {

     /**
     * 모든 게시글 목록 조회 (최신순)
     */
    List<Board> getAllPosts();

     /**
     * 유저 id 조회를 통한 특정 유저 게시글 보기
     */
    List<Board> getPostByUserId(long userId);

    /*
     * 게시글 id 조회를 통한 게시글 상세 확인
     */
    Board getPost(long postId);

     /*
     * 게시글 작성 유저 id로 조회해서 붙이기
     */

    Board createPost(long id, BoardPostDto dto); //BoardCreateRequestDto dto

     /**
     * 게시글 수정- 작성자 본인인지 확인 필요, 게시글 id로 수정
     */

    Board updatePost(long userId, long postId, BoardPostDto dto);

     /**
     * 게시글 삭제
     */

    int deletePost(long userId, long postId);

     /**
     * 게시글 조회수 증가
     */

    void increaseViewCount();

} 

