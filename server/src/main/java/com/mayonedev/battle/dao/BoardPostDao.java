package com.mayonedev.battle.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.mayonedev.battle.entity.Board;

@Mapper
public interface BoardPostDao {
    /*
     * 게시글 목록 조회
     */
    List<Board> findAll();

    /*
     * 사용자 ID로 게시글 조회
     */
    Board findByUserId(@Param("userId") long userId);

    /*
     * 게시글 ID로 게시글 조회
     */
    Board findByPostId(@Param("postId") long postId);
    
    /*
     * 닉네임으로 게시글 조회
     */

    Board findPostByNickName(@Param("nickName") String nickName);
    /*
     * 게시글 등록
     */
    int insert(Board board);

    /*
     * 게시글 수정
     */
    int update(Board board);
    /*
     * 게시글고유 아이디를 통한 게시글 삭제
     */

    int deletePostByPostId(@param("postId") long postId);
    /*
     * 총 게시글 방문 수 조회
     */
    int count();
}
