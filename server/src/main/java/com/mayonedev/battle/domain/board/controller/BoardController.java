package com.mayonedev.battle.domain.board.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mayonedev.battle.domain.board.dto.BoardPostDto;
import com.mayonedev.battle.domain.board.entity.Post;
import com.mayonedev.battle.domain.user.dto.UserDetailsDTO;
import com.mayonedev.battle.domain.user.entity.User;
import com.mayonedev.battle.domain.board.service.BoardService;
import com.mayonedev.battle.domain.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Board", description = "게시판 관리 API")
public class BoardController {
    private final BoardService boardService;
    private final UserService userService;

    @GetMapping("/post")
    @Operation(summary = "모든 게시글 조회", description = "모든 게시글 목록을 조회합니다. 페이징 파라미터(page, size)를 선택적으로 받을 수 있습니다.")
    public ResponseEntity<?> getAllPosts(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "0") int size,
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false, defaultValue = "") String sort) throws Exception {
        if (page == 0 && size == 0) {
            List<BoardPostDto> posts = boardService.selectPostAll();
            return ResponseEntity.ok(posts);
        }
        if (page < 1)
            page = 1;
        if (size < 1)
            size = 10;
        Map<String, Object> result = boardService.selectPostAllWithPaging(page, size, search, sort);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{board_id}/post/{post_id}")
    @Operation(summary = "게시글 상세 조회", description = "board_id와 post_id를 이용해 게시글을 상세 조회합니다.")
    public ResponseEntity<?> getPostByPostId(@PathVariable("board_id") long board_id,
            @PathVariable("post_id") long post_id) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("boardId", board_id);
        params.put("postId", post_id);

        boardService.increaseViewCount(params);
        BoardPostDto post = boardService.selectByPostId(params);
        Map<String, Object> map = Map.of("resmsg", "게시글 상세 조회", "resvalue", post);
        return ResponseEntity.ok(map);
    }

    @GetMapping("/post/tags/{tags}")
    @Operation(summary = "게시글 태그 조회", description = "게시글을 태그별로 조회합니다. 페이징 파라미터(page, size)를 선택적으로 받을 수 있습니다.")
    public ResponseEntity<?> getPostByTags(
            @PathVariable String tags,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "0") int size,
            @RequestParam(required = false, defaultValue = "") String keyWord,
            @RequestParam(required = false, defaultValue = "") String sort) throws Exception {
        if (page == 0 && size == 0) {
            List<BoardPostDto> posts = boardService.selectByPostTags(tags);
            Map<String, Object> map = Map.of("resmsg", "게시글 태그 조회", "resvalue", posts);
            return ResponseEntity.ok(map);
        }
        if (page < 1)
            page = 1;
        if (size < 1)
            size = 10;
        Map<String, Object> result = boardService.selectByPostTagsWithPaging(tags, page, size, keyWord, sort);
        Map<String, Object> map = Map.of("resmsg", "게시글 태그 조회", "resvalue", result);
        return ResponseEntity.ok(map);
    }

    @PostMapping("/post")
    @Operation(summary = "게시글 작성", description = "게시글을 작성 합니다.")
    public ResponseEntity<?> createBoard(@RequestBody BoardPostDto post,
            @AuthenticationPrincipal UserDetailsDTO loginuser) throws Exception {
        try {
            if (loginuser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("resmsg", "로그인이 필요합니다."));
            }

            User u = userService.getUserByEmail(loginuser.getEmail());
            long board_id = boardService.selectBoardId(post.getTags());
            Post newPost = new Post(board_id, post.getTitle(), post.getContent(), u.getUserId());

            boardService.insertPost(newPost);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("resmsg", "게시글이 등록되었습니다", "resvalue", post));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("resmsg", "게시글 등록이 실패했습니다", "resvalue", e.getMessage()));
        }
    }

    @PatchMapping("/{board_id}/post/{post_id}")
    @Operation(summary = "게시글 수정", description = "본인 게시글을 수정합니다.")
    public ResponseEntity<?> updatePost(@PathVariable("board_id") long board_id, @PathVariable("post_id") long post_id,
            @AuthenticationPrincipal UserDetailsDTO loginuser, @RequestBody BoardPostDto post) throws Exception {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("boardId", board_id);
            params.put("postId", post_id);

            BoardPostDto oldPost = boardService.selectByPostId(params);
            User u = userService.getUserByEmail(loginuser.getEmail());

            if (Long.valueOf(u.getUserId()).equals(oldPost.getUserId())) {

                Post updatePost = new Post();
                updatePost.setBoardId(board_id);
                updatePost.setPostId(post_id);
                updatePost.setTitle(post.getTitle());
                updatePost.setContent(post.getContent());
                updatePost.setBoardId(boardService.selectBoardId(post.getTags()));

                BoardPostDto updatedPost = boardService.updatePost(updatePost);
                return ResponseEntity.status(HttpStatus.OK)
                        .body(Map.of("resmsg", "게시글이 수정되었습니다.", "resvalue", updatedPost));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("resmsg", "게시글 수정에 실패했습니다", "resvalue", "게시글 작성자만 게시글을 수정할 수 있습니다."));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("resmsg", "게시글 수정에 실패했습니다", "resvalue", e.getMessage()));
        }
    }

    @DeleteMapping("/{board_id}/post/{post_id}")
    @Operation(summary = "게시글 삭제", description = "본인 게시글을 삭제합니다.")
    public ResponseEntity<?> deletePost(@PathVariable("board_id") long board_id, @PathVariable("post_id") long post_id,
            @AuthenticationPrincipal UserDetailsDTO loginuser) throws Exception {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("boardId", board_id);
            params.put("postId", post_id);

            BoardPostDto deletePost = boardService.selectByPostId(params);
            User u = userService.getUserByEmail(loginuser.getEmail());

            if (Long.valueOf(u.getUserId()).equals(deletePost.getUserId())) {
                boardService.deletePost(params);
                return ResponseEntity.status(HttpStatus.OK)
                        .body(Map.of("resmsg", "게시글이 삭제되었습니다.", "resvalue", post_id));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("resmsg", "게시글 삭제에 실패했습니다.", "resvalue", "본인이 작성한 게시글만 삭제가 가능합니다."));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("resmsg", "게시글 삭제에 실패했습니다", "value", e.getMessage()));
        }
    }
}
