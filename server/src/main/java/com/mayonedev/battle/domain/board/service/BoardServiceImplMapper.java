package com.mayonedev.battle.domain.board.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mayonedev.battle.domain.board.dao.BoardPostDao;
import com.mayonedev.battle.domain.board.dto.BoardPostDto;
import com.mayonedev.battle.domain.board.entity.Post;

@Service("BoardServiceImplMapper")
public class BoardServiceImplMapper implements BoardService {

	@Autowired
	public BoardPostDao bDao;

	@Override
	public List<BoardPostDto> selectPostAll() throws Exception {
		return bDao.selectPostAll();
	}

	@Override
	public List<BoardPostDto> selectPostByUserId(long userId) throws Exception {
		return bDao.selectPostByUserId(userId);
	}

	@Override
	public BoardPostDto selectByPostId(java.util.Map<String, Object> params) throws Exception {
		return bDao.selectByPostId(params);
	}

	@Override
	public List<BoardPostDto> selectByPostTags(String tags) throws Exception {

		if (tags.equals("general"))
			tags = "일반";
		if (tags.equals("question"))
			tags = "질문";
		if (tags.equals("tip"))
			tags = "팁";
		if (tags.equals("free"))
			tags = "자유";

		System.out.println(tags + "=======================");

		return bDao.selectByPostTags(tags);
	}

	@Override
	public List<BoardPostDto> selectPostByNickName(String nickname) throws Exception {
		return bDao.selectPostByNickName(nickname);
	}

	@Override
	public void insertPost(Post Post) throws Exception {
		bDao.insertPost(Post);
	}

	@Override
	public BoardPostDto updatePost(Post Post) throws Exception {
		bDao.updatePost(Post);
		java.util.Map<String, Object> params = new java.util.HashMap<>();
		params.put("board_id", Post.getBoard_id());
		params.put("post_id", Post.getPost_id());
		return bDao.selectByPostId(params);
	}

	@Override
	public void deletePost(java.util.Map<String, Object> params) throws Exception {
		bDao.deletePost(params);
	}

	@Override
	public void increaseViewCount(java.util.Map<String, Object> params) throws Exception {
		bDao.increaseViewCount(params);
	}

	@Override
	public long selectBoardId(String tags) {
		// 이거 여쭤보기 .. --> 여기 들어가는 게 맞을지

		if (tags.equals("general"))
			tags = "일반";
		if (tags.equals("question"))
			tags = "질문";
		if (tags.equals("tip"))
			tags = "팁";
		if (tags.equals("free"))
			tags = "자유";

		System.out.println(tags + "=======================");

		return bDao.selectBoardId(tags);
	}

}
