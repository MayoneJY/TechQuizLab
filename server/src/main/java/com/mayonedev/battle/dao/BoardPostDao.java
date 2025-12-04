package com.mayonedev.battle.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import com.mayonedev.battle.entity.Board;

@Mapper
public interface BoardPostDao {
    
    //게시글 조회(All) 
    List<Board> selectPostAll() throws Exception;

    //게시글 조회(user id)
    List<Board> selectpostbyuserid(long userId)throws Exception; 

    //게시글 조회(post id)
    Board selectByPostId(int id)throws Exception; 
    
    //게시글 조회(tags)
    List<Board> selectByPostTags(String tags) throws Exception;

    // 게시글 조회(NickName)
    List<Board> selectPostByNickName(String nickname)throws Exception;

    //게시글 작성
    void insertPost(Board board)throws Exception;

    //게시글 수정
    void updatePost(Board board)throws Exception;

    //게시글 삭제
    void deletePost(int id) throws Exception;

    // 게시글 조회수 저장
    int increaseViewCount(int id) throws Exception;

	
}
