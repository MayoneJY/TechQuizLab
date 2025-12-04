package com.mayonedev.battle.service;

import com.mayonedev.battle.entity.Board;
import java.util.*;

public interface BoardService {

    List<Board> selectPostAll() throws Exception;

    List<Board> selectpostbyuserid(long userId)throws Exception; 
    
    List<Board> selectpostByTags(String tags) throws Exception;

    Board selectByPostId(int id)throws Exception; 

    List<Board> selectPostByNickName(String nickname)throws Exception;

    void insertPost(Board board)throws Exception;

    void updatePost(Board board)throws Exception;

    void deletePost(int id) throws Exception;

    int increaseViewCount(int id) throws Exception;
} 

