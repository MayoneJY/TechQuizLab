package com.mayonedev.battle.controller;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mayonedev.battle.dto.UserDetailsDTO;
import com.mayonedev.battle.entity.Board;
import com.mayonedev.battle.entity.User;
import com.mayonedev.battle.service.BoardService;
import com.mayonedev.battle.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.server.PathParam;
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

    // 모든 게시글 조회
    @GetMapping("/post")
    @Operation(summary = "모든 게시글 조회", description = "모든 게시글 목록을 조회합니다.")
    public ResponseEntity<List<Board>> getAllPosts() throws Exception {
        System.out.println("목록조회까지옴.");
        List<Board> boards = boardService.selectPostAll();
        System.out.println(boards);
        return ResponseEntity.ok(boards);
    }

    // 게시글 상세 조회
    @GetMapping("/post/{id}")
    @Operation(summary = "게시글 상세 조회", description = "postId 를 이용해 게시글을 상세 조회 합니다.")
    public ResponseEntity<?> getPostByPostId(@PathVariable int id) throws Exception {

        boardService.increaseViewCount(id); // 조회수 카운트
        Board board = boardService.selectByPostId(id);

        System.out.println(board);

        Map<String, Object> map = Map.of("resmsg", "게시글 상세 조회", "resvalue", board);
        return ResponseEntity.ok(map);
    }

    // 게시글 태그별 조회
    @GetMapping("/post/tags/{tags}")
    @Operation(summary = "게시글 태그 조회", description = "게시글을 태그별로 조회합니다.")
    public ResponseEntity<?> getPostByTags(@PathVariable String tags) throws Exception {
        System.out.println("태그조회까지옴");
        List<Board> boards = boardService.selectpostByTags(tags);
        System.out.println(boards);
        Map<String, Object> map = Map.of("resmsg", "게시글 태그 조회", "resvalue", boards);
        return ResponseEntity.ok(map);
    }

    // 게시글 작성
    @PostMapping("/post")
    @Operation(summary = "게시글 작성", description = "게시글을 작성 합니다.")
    public ResponseEntity<?> createBoard(@RequestBody Board b, @AuthenticationPrincipal UserDetailsDTO loginuser)
            throws Exception {
        try {
            if (loginuser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("resmsg", "로그인이 필요합니다."));
            }
            User u = userService.getUserByEmail(loginuser.getEmail());
            b.setUserId(u.getUserId());
            boardService.insertPost(b);
            Map<String, Object> map = Map.of("resmsg", "게시글이 등록되었습니다", "resvalue", b);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(map);

        } catch (RuntimeException e) {
            Map<String, Object> err = Map.of("resmsg", "게시글 등록이 실패했습니다",
                    "resvalue", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }
    }

    // 게시글 수정 - 현재 세션 기반 유저 검사
    @PatchMapping("/post/{id}")
    @Operation(summary = "게시글 수정", description = "본인 게시글을 수정합니다.")
    public ResponseEntity<?> updatePost(@PathVariable int id, @AuthenticationPrincipal UserDetailsDTO loginuser)
            throws Exception {
        try {
            Board b = boardService.selectByPostId(id);
            // 여기에 서비스 선언해서 유저 DTO 에서 user id 조회해가지고 둘이 맞는지 검사하면 된다 하 ..
            User u = userService.getUserByEmail(loginuser.getEmail());

            if (u.getUserId() == b.getUserId()) {
                boardService.updatePost(b);
                Map<String, Object> map = Map.of("resmsg", "게시글이 수정되었습니다.", "resvalue", b);
                return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(map);
            } else {
                Map<String, Object> err = Map.of("resmsg", "게시글 수정에 실패했습니다",
                        "resvalue", "게시글 작성자만 게시글을 수정할 수 있습니다.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(err);
            }

        } catch (RuntimeException e) {
            Map<String, Object> err = Map.of("resmsg", "게시글 수정에 실패했습니다",
                    "resvalue", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }

    }

    // 게시글 삭제
    @DeleteMapping("/post/{id}")
    @Operation(summary = "게시글 삭제", description = "본인 게시글을 삭제합니다.")
    public ResponseEntity<?> deletePost(@PathVariable int id, UserDetailsDTO loginuser) throws Exception {
        try {

            Board b = boardService.selectByPostId(id);
            User u = userService.getUserByEmail(loginuser.getEmail());

            if (u.getUserId() == b.getUserId()) {
                boardService.deletePost(id);
                Map<String, Object> map = Map.of("resmsg", "게시글이 삭제되었습니다.", "resvalue", id);
                return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(map);
            } else {
                Map<String, Object> err = Map.of("resmsg", "게시글 삭제에 실패했습니다.", "resvalue", "본인이 작성한 게시글만 삭제가 가능합니다.");
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN).body(err);
            }

        } catch (RuntimeException e) {
            Map<String, Object> err = Map.of("resmsg", "게시글 삭제에 실패했습니다",
                    "resvalue", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }
    }

}
