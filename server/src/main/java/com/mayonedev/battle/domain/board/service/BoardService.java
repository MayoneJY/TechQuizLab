package com.mayonedev.battle.domain.board.service;

import java.util.List;
import java.util.Map;

import com.mayonedev.battle.domain.board.dto.BoardPostDto;
import com.mayonedev.battle.domain.board.entity.Post;

public interface BoardService {

    List<BoardPostDto> selectPostAll() throws Exception;

    public Map<String, Object> selectPostAllWithPaging(int page, int size, String search, String sort) throws Exception;

    List<BoardPostDto> selectPostByUserId(long userId) throws Exception;

    BoardPostDto selectByPostId(Map<String, Object> params) throws Exception;

    List<BoardPostDto> selectByPostTags(String tags) throws Exception;

    public Map<String, Object> selectByPostTagsWithPaging(String tags, int page, int size, String search, String sort)
            throws Exception;

    List<BoardPostDto> selectPostByNickName(String nickname) throws Exception;

    void insertPost(Post post) throws Exception;

    BoardPostDto updatePost(Post Post) throws Exception;

    void deletePost(Map<String, Object> params) throws Exception;

    void increaseViewCount(Map<String, Object> params) throws Exception;

    long selectBoardId(String tags);
}
