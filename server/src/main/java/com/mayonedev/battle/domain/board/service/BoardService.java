package com.mayonedev.battle.domain.board.service;

import com.mayonedev.battle.domain.board.dto.BoardPostDto;
import com.mayonedev.battle.domain.board.entity.Post;

import java.util.*;

public interface BoardService {

    List<BoardPostDto> selectPostAll() throws Exception;

    List<BoardPostDto> selectPostByUserId(long userId) throws Exception;

    BoardPostDto selectByPostId(java.util.Map<String, Object> params) throws Exception;

    List<BoardPostDto> selectByPostTags(String tags) throws Exception;

    List<BoardPostDto> selectPostByNickName(String nickname) throws Exception;

    void insertPost(Post post) throws Exception;

    BoardPostDto updatePost(Post Post) throws Exception;

    void deletePost(java.util.Map<String, Object> params) throws Exception;

    void increaseViewCount(java.util.Map<String, Object> params) throws Exception;

    long selectBoardId(String tags);
}
