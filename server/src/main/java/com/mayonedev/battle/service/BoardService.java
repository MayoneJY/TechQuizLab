package com.mayonedev.battle.service;

import com.mayonedev.battle.entity.Board;
import java.util.*;

public interface BoardService { //매퍼가 서비스랑 어떻게 연결될까?

    //게시글 목록 조회
    List<Board> selectPostAll() throws Exception;

    //사용자 ID로 게시글 조회
    List<Board> selectpostbyuserid(int userId)throws Exception; 

    //게시글 ID로 게시글 조회
    Board selectByPostId(int id)throws Exception; 

    // TODO 닉네임으로 게시글 조회
    List<Board> selectPostByNickName(String nickname)throws Exception;

    //게시글 등록
    void insertPost(Board board)throws Exception;

    //게시글 수정 - 본인확인 필요 : 이것도 오늘 배운 인증 으로 하게 되는지??
    void updatePost(Board board)throws Exception;

    //게시글 삭제
    void deletePost(int id) throws Exception;

    // TODO 총 게시글 방문 수 조회 - 미구현
    int increaseViewCount() throws Exception;
} 

