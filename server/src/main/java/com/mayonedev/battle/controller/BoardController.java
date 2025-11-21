package com.mayonedev.battle.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mayonedev.battle.entity.Board;
import com.mayonedev.battle.service.BoardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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

    // 모든 게시글 조회 
    @GetMapping("/post")
    @Operation(summary = "게시글 목록 조회", description = "모든 게시글 목록을 조회합니다.")
    public ResponseEntity<List<Board>> getAllPosts() throws Exception{
        List<Board> boards = boardService.selectPostAll();
        return ResponseEntity.ok(boards);
    }

    // TODO 자기 게시글 보기
    // @PostMapping("/")
    // @Operation(summary = "게시글 상세 조회", description = "userId 를 이용해 게시글을 상세 조회 합니다.")
    // public ResponseEntity<List<Board>> getPostsByUserId(
    //     @Parameter(description = "사용자 ID") @PathVariable long id){

    //         List<Board> board = boardService.getPostByUserId(id);

    //         if(board.isEmpty()){
    //             return ResponseEntity.notFound().build(); // TODO: Q. 이게 무슨 코드일까...
    //         }

    //         return ResponseEntity.ok(board);
    // }

    // 게시글 상세 조회
    @GetMapping("/post/{id}")
    @Operation(summary = "게시글 상세 조회", description = "postId 를 이용해 게시글을 상세 조회 합니다.")
    public ResponseEntity<Board> getPostByPostId( @PathVariable int id) throws Exception{
        Board board = boardService.selectByPostId(id);
        return ResponseEntity.ok(board);
    }

    // 게시글 작성 - 유저 id?
    @PostMapping("/post")
    @Operation(summary = "게시글 작성", description = "게시글을 작성 합니다.")
    public ResponseEntity<?> craeteBoard(@RequestBody Board b) throws Exception{
        try {
            boardService.insertPost(b);
		    Map<String, Object> map= Map.of("resmsg","게시글이 등록되었습니다","resvalue", b);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(map);

        } catch (RuntimeException e) {
            Map<String, Object> err = Map.of("resmsg", "게시글 등록이 실패했습니다",
                                             "resvalue", e.getMessage() );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }
    }
    // 게시글 수정 - 자기 게시글만 수정 가능하게끔...
    @PatchMapping("/post/{id}")
    @Operation(summary = "게시글 수정", description = "게시글을 수정합니다.")
    public ResponseEntity<?> updatePost(@PathVariable int id, @RequestBody Board b) throws Exception{
       try {
            boardService.updatePost(b);
            Map<String, Object> map = Map.of("resmsg", "게시글이 수정되었습니다.", "resvalue", b);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(map);

        } catch (RuntimeException e) {
            Map<String, Object> err = Map.of("resmsg", "게시글 수정에 실패했습니다",
                                             "resvalue", e.getMessage() );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
       }

    }

    // 게시글 삭제
    @DeleteMapping("/post/{id}")
    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    public ResponseEntity<?> deletePost( @PathVariable int id) throws Exception{
        try {
            boardService.deletePost(id);
            Map<String, Object> map = Map.of("resmsg", "게시글이 삭제되었습니다.", "resvalue", id);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(map);
        } catch (RuntimeException e) {
                Map<String, Object> err = Map.of("resmsg", "게시글 삭제에 실패했습니다",
                                                 "resvalue", e.getMessage() );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }
    }


}
