package com.mayonedev.battle.domain.board.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.mayonedev.battle.domain.board.dto.BoardPostDto;
import com.mayonedev.battle.domain.board.entity.Post;

@Mapper
public interface BoardPostDao {

    // 게시글 조회(All)
    List<BoardPostDto> selectPostAll() throws Exception;

    // 게시글 조회(user id)
    List<BoardPostDto> selectPostByUserId(long userId) throws Exception;

    // 게시글 조회(post id) - 복합키 사용
    BoardPostDto selectByPostId(Map<String, Object> params) throws Exception;

    // 게시글 조회(tags)
    List<BoardPostDto> selectByPostTags(String tags) throws Exception;

    // 게시글 조회(NickName)
    List<BoardPostDto> selectPostByNickName(String nickname) throws Exception;

    // 게시글 작성
    void insertPost(Post post) throws Exception;

    // 게시글 수정
    void updatePost(Post post) throws Exception;

    // 게시글 삭제 - 복합키 사용
    void deletePost(Map<String, Object> params) throws Exception;

    // 게시글 조회수 저장 - 복합키 사용
    int increaseViewCount(Map<String, Object> params) throws Exception;

    // 게시글 카테고리 아이디 검색
    long selectBoardId(String tags);

}
