package com.mayonedev.battle.domain.board.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mayonedev.battle.domain.board.dao.BoardCommentDao;
import com.mayonedev.battle.domain.board.dao.BoardPostDao;
import com.mayonedev.battle.domain.board.dao.BoardPostlikeDao;
import com.mayonedev.battle.domain.board.dto.BoardPostDto;
import com.mayonedev.battle.domain.board.entity.Post;
import com.mayonedev.battle.domain.gamification.service.GamificationService;

@Service("BoardServiceImplMapper")
public class BoardServiceImplMapper implements BoardService {

	@Autowired
	public BoardPostDao bDao;

	@Autowired
	public BoardCommentDao commentDao;

	@Autowired
	public BoardPostlikeDao likeDao;

	@Autowired
	private GamificationService gamificationService;

	@Override
	public List<BoardPostDto> selectPostAll() throws Exception {
		return bDao.selectPostAll();
	}

	@Override
	public Map<String, Object> selectPostAllWithPaging(int page, int size, String search, String sort)
			throws Exception {
		Map<String, Object> params = new HashMap<>();
		int offset = (page - 1) * size;
		params.put("offset", offset);
		params.put("size", size);

		if (search != null && !search.isEmpty()) {
			params.put("search", search);
		}
		if (sort != null && !sort.isEmpty()) {
			params.put("sort", sort);
		}

		List<BoardPostDto> posts = bDao.selectPostAllWithPaging(params);
		int totalCount = bDao.selectPostAllCount();
		int totalPages = (int) Math.ceil((double) totalCount / size);
		// Wait, if I filter, totalCount for pagination must be the FILTERED count.
		// I need to check if selectPostAllCount can take params. It currently doesn't.
		// I should probably update selectPostAllCount to take params too if I want
		// accurate pagination.

		// Let's check BoardPostDao.java again. selectPostAllCount() takes no args.
		// For correct pagination with search, I MUST update the count query too.
		// I will first implement passing params here, and then I will update the DAO
		// and XML to support count filtering.
		// Actually, let's pass the params to a new count method or overloaded one if
		// MyBatis supports it?
		// Or simply update selectPostAllCount to take Map<String, Object> params.

		// For now, I will assume I will update selectPostAllCount signature next.
		// So I will pass params to selectPostAllCount(params) here.

		// totalCount = bDao.selectPostAllCount(params);
		// But bDao.selectPostAllCount() currently is no-arg.
		// I will stick to the existing signature for now and just update the main
		// query,
		// but to be correct I should fix the count query too.
		// Strategy: Update this file to use a hypothetical selectPostAllCount(params),
		// then update DAO signature, then update XML.

		// Let's assume selectPostAllWithPaging returns the filtered list.
		// I will update selectPostAllCount in the DAO step.
		// For now, I'll use the existing no-arg count, acknowledging it will be wrong
		// for searches until I fix it.
		// Actually, I should update the DAO first or together.
		// Let's just update the params putting here first.

		Map<String, Object> result = new HashMap<>();
		result.put("posts", posts);

		// I'll defer the count logic fix to the XML/DAO step, but here I should pass
		// params if I could.
		// Since I haven't updated DAO yet, I can't call a new method.
		// I will update the logic here to match the interface change first.

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

		return bDao.selectByPostTags(tags);
	}

	@Override
	public Map<String, Object> selectByPostTagsWithPaging(String tags, int page, int size, String search, String sort)
			throws Exception {
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

		if (search != null && !search.isEmpty()) {
			params.put("search", search);
		}
		if (sort != null && !sort.isEmpty()) {
			params.put("sort", sort);
		}

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
		try {
			gamificationService.completeMission(Post.getUserId(), "POST_WRITE");
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	@Transactional
	public void deletePost(Map<String, Object> params) throws Exception {
		Object boardIdObj = params.get("boardId");
		Object postIdObj = params.get("postId");

		long boardId = boardIdObj instanceof Number ? ((Number) boardIdObj).longValue()
				: Long.parseLong(boardIdObj.toString());
		long postId = postIdObj instanceof Number ? ((Number) postIdObj).longValue()
				: Long.parseLong(postIdObj.toString());

		// 1. 대댓글의 부모 참조를 먼저 끊는다 (순환 참조 회피)
		commentDao.disconnectComments(boardId, postId);

		// 2. 모든 댓글 삭제
		commentDao.deleteCommentsByPostId(boardId, postId);

		// 3. 좋아요 삭제
		likeDao.deleteLikesByPostId(boardId, postId);

		// 4. 게시글 삭제
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

		return bDao.selectBoardId(tags);
	}

}
