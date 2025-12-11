package com.mayonedev.battle.domain.board.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	public Map<String, Object> selectPostAllWithPaging(int page, int size) throws Exception {
		Map<String, Object> params = new HashMap<>();
		int offset = (page - 1) * size;
		params.put("offset", offset);
		params.put("size", size);

		List<BoardPostDto> posts = bDao.selectPostAllWithPaging(params);
		int totalCount = bDao.selectPostAllCount();
		int totalPages = (int) Math.ceil((double) totalCount / size);

		Map<String, Object> result = new HashMap<>();
		result.put("posts", posts);
		result.put("totalCount", totalCount);
		result.put("totalPages", totalPages);
		result.put("currentPage", page);
		result.put("size", size);

		return result;
	}

	@Override
	public List<BoardPostDto> selectPostByUserId(long userId) throws Exception {
		return bDao.selectPostByUserId(userId);
	}

	@Override
	public BoardPostDto selectByPostId(Map<String, Object> params) throws Exception {
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
	public Map<String, Object> selectByPostTagsWithPaging(String tags, int page, int size) throws Exception {
		if (tags.equals("general"))
			tags = "일반";
		if (tags.equals("question"))
			tags = "질문";
		if (tags.equals("tip"))
			tags = "팁";
		if (tags.equals("free"))
			tags = "자유";

		Map<String, Object> params = new HashMap<>();
		int offset = (page - 1) * size;
		params.put("offset", offset);
		params.put("size", size);
		params.put("tags", tags);

		List<BoardPostDto> posts = bDao.selectByPostTagsWithPaging(params);
		int totalCount = bDao.selectByPostTagsCount(tags);
		int totalPages = (int) Math.ceil((double) totalCount / size);

		Map<String, Object> result = new HashMap<>();
		result.put("posts", posts);
		result.put("totalCount", totalCount);
		result.put("totalPages", totalPages);
		result.put("currentPage", page);
		result.put("size", size);

		return result;
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
		Map<String, Object> params = new HashMap<>();
		params.put("boardId", Post.getBoardId());
		params.put("postId", Post.getPostId());
		return bDao.selectByPostId(params);
	}

	@Override
	public void deletePost(Map<String, Object> params) throws Exception {
		bDao.deletePost(params);
	}

	@Override
	public void increaseViewCount(Map<String, Object> params) throws Exception {
		bDao.increaseViewCount(params);
	}

	@Override
	public long selectBoardId(String tags) {
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
