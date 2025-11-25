package com.mayonedev.battle.service;

import com.mayonedev.battle.entity.Board;
import java.util.*;

public interface BoardService {

    List<Board> selectPostAll() throws Exception;

    List<Board> selectpostbyuserid(int userId)throws Exception; 

    Board selectByPostId(int id)throws Exception; 

    List<Board> selectPostByNickName(String nickname)throws Exception;

    void insertPost(Board board)throws Exception;

    void updatePost(Board board)throws Exception;

    void deletePost(int id) throws Exception;

    // 게시글 조회수 카운트
    int increaseViewCount(int id) throws Exception;
} 

