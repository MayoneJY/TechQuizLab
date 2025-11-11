package com.mayonedev.battle.service;

import com.mayonedev.battle.dto.BoardPostDto;
import com.mayonedev.battle.entity.Board;
import java.util.*;

public interface BoardService {

     /**
     * 모든 게시글 목록 조회 (최신순)
     * - 목록화면에서 보여줄 데이터들
     */
     List<Board> getAllPosts();

    /**
     * 유저 id 조회를 통한 게시글 상세 확인
     */

     Board getPost(Long id);
     /**
     * 게시글 작성
     */

     Board createPost(long id, BoardPostDto dto); //BoardCreateRequestDto dto

     /**
     * 게시글 수정- 작성자 본인인지 확인 필요
     */

     Board updatePost(long id, BoardPostDto dto);

     /**
     * 게시글 삭제
     */

    Board deletePost(long id, BoardPostDto dto);

     /**
     * 게시글 조회수 증가
     */

    void increaseViewCount();

} 

