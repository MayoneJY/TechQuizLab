package com.mayonedev.battle.domain.board.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.mayonedev.battle.domain.board.dto.BoardPostlikeDto;
import com.mayonedev.battle.domain.board.entity.PostLike;

@Mapper
public interface BoardPostlikeDao {

	// 좋아요 추가
	void insertPostLike(PostLike postLike) throws Exception;

	// 좋아요 삭제
	void deletePostLike(BoardPostlikeDto postlikeParm) throws Exception;

	// 게시글의 좋아요 개수 조회
	int countPostLike(BoardPostlikeDto postlikeParm) throws Exception;

	// 사용자가 특정 게시글에 좋아요를 눌렀는지 확인
	int checkPostLike(BoardPostlikeDto postlikeParm) throws Exception;

	// 게시글의 좋아요 목록 조회
	List<PostLike> selectPostLikeByPost(BoardPostlikeDto postlikeParm) throws Exception;

	// 사용자가 좋아요한 게시글 목록 조회
	List<PostLike> selectPostLikeByUser(long user_id) throws Exception;

}

