package com.mayonedev.battle.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mayonedev.battle.dto.BoardPostDto;
import com.mayonedev.battle.entity.Board;
import com.mayonedev.battle.service.BoardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController // TODO: Q.Http 응답을 받아서 Json으로 돌려주는 역할(리액트 사용?)
@RequestMapping("/api/boards") //Q. 왜 api 를 경로로 붙이는 걸까요,,
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Board", description = "게시판 관리 API")//Q. 
public class BoardController {
    
    private final BoardService boardService;

    // 모든 게시글 조회 
    // TODO: Q. ResponseEntity 이것이 무엇일까요 ????? 
    @GetMapping
    @Operation(summary = "게시글 목록 조회", description = "모든 게시글 목록을 조회합니다.")
    public ResponseEntity<List<Board>> getAllPosts(){
        List<Board> boards = boardService.getAllPosts();
        return ResponseEntity.ok(boards); //Q. 리액트를 써서 나오는 코드 일지 ...
    }

    // TODO 자기 게시글 보기
    @GetMapping
    @Operation
    public ResponseEntity<List<Board>> getPostsByUserId(
        @Parameter(description = "사용자 ID") @PathVariable long id){

            List<Board> board = boardService.getPostByUserId(id);

            if(board.isEmpty()){
                return ResponseEntity.notFound().build(); // TODO: Q. 이게 무슨 코드일까...
            }

            return ResponseEntity.ok(board);
    }

    // 게시글 상세 조회
    @GetMapping
    @Operation(summary = "게시글 상세 조회", description = "postId 를 이용해 게시글을 상세 조회 합니다.")
    public ResponseEntity<Board> getPostByPostId(
        @Parameter @PathVariable long id
    ){
        Board board = boardService.getPost(id);
        return ResponseEntity.ok(board);
    }

    // 게시글 작성
    @PostMapping
    @Operation
    public ResponseEntity<?> craeteBoard(
        @Parameter @PathVariable long userId,
        @RequestBody BoardPostDto boardDto){
        try {
            Board createBoard = boardService.createPost(userId, boardDto);
            return ResponseEntity.ok(createBoard);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // 게시글 수정
    @PostMapping
    @Operation
    public ResponseEntity<?> updatePost(
        @Parameter @PathVariable long userId,
        @Parameter @PathVariable long postId,
        @RequestBody BoardPostDto boardDto
    ){
       try {
            Board updateBoard = boardService.updatePost(userId, postId, boardDto);
            return ResponseEntity.ok(updateBoard);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
       }

    }

    // 게시글 삭제
    public ResponseEntity<?> deletePost(
        @Parameter @PathVariable long userId,
        @Parameter @PathVariable long postId
    ){
        try {
            int result = boardService.deletePost(userId, postId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
