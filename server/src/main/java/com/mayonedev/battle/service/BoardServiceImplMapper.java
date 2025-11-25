package com.mayonedev.battle.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mayonedev.battle.dao.BoardPostDao;
import com.mayonedev.battle.dto.BoardPostDto;
import com.mayonedev.battle.entity.Board;

@Service("BoardServiceImplMapper")
public class BoardServiceImplMapper implements BoardService{

    @Autowired
    public BoardPostDao bDao;

    @Override
    public List<Board> selectPostAll() throws Exception {
        return bDao.selectPostAll();
    }

    @Override
    public List<Board> selectpostbyuserid(int userId) throws Exception {
        return bDao.selectpostbyuserid(userId);
    }

    @Override
    public Board selectByPostId(int id) throws Exception {
        return bDao.selectByPostId(id);
    }

    @Override
    public List<Board> selectPostByNickName(String nickname) throws Exception {
  
        return bDao.selectPostByNickName(nickname);
    }

    @Override
    public void insertPost(Board board) throws Exception {
 
        bDao.insertPost(board);
    }

    @Override
    public void updatePost(Board board) throws Exception {
        bDao.updatePost(board);
    }

    @Override
    public void deletePost(int id) throws Exception {
        bDao.deletePost(id);
    }

    @Override
    public int increaseViewCount(int id) throws Exception {
       return bDao.increaseViewCount(id);
    }

}
