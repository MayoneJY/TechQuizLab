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
    	System.out.println("서비스 까지 들어옴");
        try {
        	return bDao.selectPostAll();
        } catch (Exception e) {
            e.printStackTrace();  // 🔥 여기서 콘솔에 SQL / NPE 원인 다 나옴
            throw e;
        }
        
    }

    @Override
    public List<Board> selectpostbyuserid(long userId) throws Exception {
        return bDao.selectpostbyuserid(userId);
    }

    @Override
    public Board selectByPostId(int id) throws Exception {
        return bDao.selectByPostId(id);
    }
    
	@Override
	public List<Board> selectpostByTags(String tags) throws Exception {
    	System.out.println("태그 서비스 까지 들어옴");
        try {
        	return bDao.selectByPostTags(tags);
        } catch (Exception e) {
            e.printStackTrace();  // 🔥 여기서 콘솔에 SQL / NPE 원인 다 나옴
            throw e;
        }
		
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
