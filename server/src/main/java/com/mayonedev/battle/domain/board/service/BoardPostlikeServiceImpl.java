package com.mayonedev.battle.domain.board.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mayonedev.battle.domain.board.dao.BoardPostlikeDao;
import com.mayonedev.battle.domain.board.dto.BoardPostlikeDto;
import com.mayonedev.battle.domain.board.entity.PostLike;

@Service("BoardPostlikeServiceImpl")
public class BoardPostlikeServiceImpl implements BoardPostlikeService {

	@Autowired
	public BoardPostlikeDao boardPostlikeDao;

	@Override
	public void insertPostLike(PostLike postLike) throws Exception {
		boardPostlikeDao.insertPostLike(postLike);
	}

	@Override
	public void deletePostLike(BoardPostlikeDto postlikeParm) throws Exception {
		boardPostlikeDao.deletePostLike(postlikeParm);
	}

	@Override
	public int countPostLike(BoardPostlikeDto postlikeParm) throws Exception {
		return boardPostlikeDao.countPostLike(postlikeParm);
	}

	@Override
	public boolean checkPostLike(BoardPostlikeDto postlikeParm) throws Exception {
		int count = boardPostlikeDao.checkPostLike(postlikeParm);
		return count > 0;
	}

	@Override
	public List<PostLike> selectPostLikeByPost(BoardPostlikeDto postlikeParm) throws Exception {
		return boardPostlikeDao.selectPostLikeByPost(postlikeParm);
	}

	@Override
	public List<PostLike> selectPostLikeByUser(long user_id) throws Exception {
		return boardPostlikeDao.selectPostLikeByUser(user_id);
	}

}

