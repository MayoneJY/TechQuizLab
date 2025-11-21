package com.mayonedev.battle.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import com.mayonedev.battle.entity.Board;

@Mapper
public interface BoardPostDao {
    
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

    //게시글 수정
    void updatePost(Board board)throws Exception;

    //게시글 삭제
    void deletePost(int id) throws Exception;

    // TODO 총 게시글 방문 수 조회 - 미구현
    int increaseViewCount() throws Exception;
}
