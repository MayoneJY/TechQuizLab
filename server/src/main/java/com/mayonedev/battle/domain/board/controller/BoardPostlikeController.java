package com.mayonedev.battle.domain.board.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

import com.mayonedev.battle.domain.board.dto.BoardPostlikeDto;
import com.mayonedev.battle.domain.board.entity.PostLike;
import com.mayonedev.battle.domain.board.service.BoardPostlikeService;
import com.mayonedev.battle.domain.user.dto.UserDetailsDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Board Post Like", description = "게시글 좋아요 관리 API")
public class BoardPostlikeController {
	private final BoardPostlikeService boardPostlikeService;

	@PostMapping("/{board_id}/post/{post_id}/like")
	@Operation(summary = "게시글 좋아요 추가", description = "특정 게시글에 좋아요를 추가합니다.")
	public ResponseEntity<?> addPostLike(@PathVariable("board_id") int board_id,
			@PathVariable("post_id") long post_id, @AuthenticationPrincipal UserDetailsDTO loginuser) throws Exception {

		BoardPostlikeDto PostlikeParm = new BoardPostlikeDto(board_id, post_id, loginuser.getUserId());
		Boolean is_like = boardPostlikeService.checkPostLike(PostlikeParm);

		if (is_like) {
			Map<String, Object> map = Map.of("resmsg", "이미 \"좋아요\" 한 게시글 입니다. ", "resvalue", "이미 \"좋아요\" 한 게시글 입니다.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
		}

		try {
			PostLike postlike = new PostLike(board_id, post_id, loginuser.getUserId());
			boardPostlikeService.insertPostLike(postlike);
			
			BoardPostlikeDto countParam = new BoardPostlikeDto(board_id, post_id);
			int likecnt = boardPostlikeService.countPostLike(countParam);
			
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(Map.of("resmsg", "좋아요가 추가되었습니다.", "resvalue", Map.of("like_count", likecnt, "is_liked", true)));
		} catch (Exception e) {
			Map<String, Object> map = Map.of("resmsg", "좋아요 추가에 실패했습니다.", "resvalue", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
		}

	}

	@DeleteMapping("/{board_id}/post/{post_id}/like")
	@Operation(summary = "게시글 좋아요 취소", description = "특정 게시글의 좋아요를 취소합니다.")
	public ResponseEntity<?> removePostLike(@PathVariable("board_id") int board_id,
			@PathVariable("post_id") long post_id, @AuthenticationPrincipal UserDetailsDTO loginuser) throws Exception {

		BoardPostlikeDto PostlikeParm = new BoardPostlikeDto(board_id, post_id, loginuser.getUserId());
		Boolean is_like = boardPostlikeService.checkPostLike(PostlikeParm);

		if (!is_like) {
			Map<String, Object> map = Map.of("resmsg", "좋아요를 누르지 않은 게시글입니다.", "resvalue", "좋아요를 누르지 않은 게시글입니다.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
		}

		try {
			boardPostlikeService.deletePostLike(PostlikeParm);
			
			BoardPostlikeDto countParam = new BoardPostlikeDto(board_id, post_id);
			int likecnt = boardPostlikeService.countPostLike(countParam);
			
			return ResponseEntity.status(HttpStatus.OK)
					.body(Map.of("resmsg", "좋아요가 취소되었습니다.", "resvalue", Map.of("like_count", likecnt, "is_liked", false)));
		} catch (Exception e) {
			Map<String, Object> map = Map.of("resmsg", "좋아요 취소에 실패했습니다.", "resvalue", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(map);
		}

	}

	@GetMapping("/{board_id}/post/{post_id}/like/count")
	@Operation(summary = "게시글 좋아요 개수 조회", description = "특정 게시글의 좋아요 개수를 조회합니다.")
	public ResponseEntity<?> getPostLikeCount(@PathVariable("board_id") int board_id,
			@PathVariable("post_id") long post_id) throws Exception {
		try {
			BoardPostlikeDto postlike = new BoardPostlikeDto(board_id, post_id);
			int likecnt = boardPostlikeService.countPostLike(postlike);
			
			Map<String, Object> map = Map.of("resmsg", "좋아요 개수 조회", "resvalue", Map.of("like_count", likecnt));
			return ResponseEntity.ok(map);
		} catch (Exception e) {
			Map<String, Object> map = Map.of("resmsg", "좋아요 개수 조회에 실패했습니다.", "resvalue", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(map);
		}
	}

	@GetMapping("/{board_id}/post/{post_id}/like/check")
	@Operation(summary = "게시글 좋아요 여부 확인", description = "현재 사용자가 특정 게시글에 좋아요를 눌렀는지 확인합니다.")
	public ResponseEntity<?> checkPostLike(@PathVariable("board_id") int board_id,
			@PathVariable("post_id") long post_id, @AuthenticationPrincipal UserDetailsDTO loginuser) throws Exception {
		try {
			if (loginuser == null) {
				return ResponseEntity.ok(Map.of("resmsg", "좋아요 여부 확인", "resvalue", Map.of("is_liked", false)));
			}

			BoardPostlikeDto PostlikeParm = new BoardPostlikeDto(board_id, post_id, loginuser.getUserId());
			Boolean is_like = boardPostlikeService.checkPostLike(PostlikeParm);

			Map<String, Object> map = Map.of("resmsg", "좋아요 여부 확인", "resvalue", Map.of("is_liked", is_like));
			return ResponseEntity.ok(map);
		} catch (Exception e) {
			Map<String, Object> map = Map.of("resmsg", "좋아요 여부 확인에 실패했습니다.", "resvalue", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(map);
		}
	}

	@GetMapping("/{board_id}/post/{post_id}/like")
	@Operation(summary = "게시글 좋아요 목록 조회", description = "특정 게시글의 좋아요 목록을 조회합니다.")
	public ResponseEntity<?> getPostLikeList(@PathVariable("board_id") int board_id,
			@PathVariable("post_id") long post_id) throws Exception {
		try {
			BoardPostlikeDto postlikeParam = new BoardPostlikeDto(board_id, post_id);
			java.util.List<PostLike> likeList = boardPostlikeService.selectPostLikeByPost(postlikeParam);

			Map<String, Object> map = Map.of("resmsg", "좋아요 목록 조회", "resvalue", likeList);
			return ResponseEntity.ok(map);
		} catch (Exception e) {
			Map<String, Object> map = Map.of("resmsg", "좋아요 목록 조회에 실패했습니다.", "resvalue", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(map);
		}
	}

	//임시 사용 X
	@GetMapping("/post/like/my")
	@Operation(summary = "내가 좋아요한 게시글 목록 조회", description = "현재 사용자가 좋아요한 게시글 목록을 조회합니다.")
	public ResponseEntity<?> getMyLikedPosts(@AuthenticationPrincipal UserDetailsDTO loginuser) throws Exception {
		try {
			List<PostLike> likedPosts = boardPostlikeService.selectPostLikeByUser(loginuser.getUserId());
			Map<String, Object> map = Map.of("resmsg", "내가 좋아요한 게시글 목록 조회", "resvalue", likedPosts);
			return ResponseEntity.ok(map);
		} catch (Exception e) {
			Map<String, Object> map = Map.of("resmsg", "좋아요한 게시글 목록 조회에 실패했습니다.", "resvalue", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(map);
		}
	}
}
