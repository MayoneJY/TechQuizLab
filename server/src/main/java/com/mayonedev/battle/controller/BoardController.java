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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mayonedev.battle.entity.Board;
import com.mayonedev.battle.entity.User;
import com.mayonedev.battle.service.BoardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
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

    // 게시글 상세 조회
    @GetMapping("/post/{id}")
    @Operation(summary = "게시글 상세 조회", description = "postId 를 이용해 게시글을 상세 조회 합니다.")
    public ResponseEntity<Board> getPostByPostId( @PathVariable int id) throws Exception{
    	 
    	 boardService.increaseViewCount(id); //조회수 카운트
    	 //근데 얘도 sql 에서 update 인건 똑같은데 왜 getmapping이지.. 게시글 수정이랑 뭐가 다른 걸까..
    	 
    	 Board board = boardService.selectByPostId(id);
    	 
    	 System.out.println(board);
        
        Map<String, Object> map = Map.of( "resmsg", "게시글 상세 조회", "resvalue", board);
        return ResponseEntity.ok(board);
    }

    // 게시글 작성
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
    // 게시글 수정 - 현재 세션 기반 유저 검사 
    @PatchMapping("/post/{id}")
    @Operation(summary = "게시글 수정", description = "본인 게시글을 수정합니다.")
    public ResponseEntity<?> updatePost(@PathVariable int id, @RequestBody Board b, HttpSession session) throws Exception{
       try {
    	    User loginUser = (User) session.getAttribute("loginUser");
    	    if(loginUser.getId()==b.getId()) {
    	    	boardService.updatePost(b);
                Map<String, Object> map = Map.of("resmsg", "게시글이 수정되었습니다.", "resvalue", b);
                return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(map);
    	    }else {
    	    	Map<String, Object> err = Map.of("resmsg", "게시글 수정에 실패했습니다",
                        "resvalue", "게시글 작성자만 게시글을 수정할 수 있습니다." );
    	    		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(err);
    	    }

        } catch (RuntimeException e) {
            Map<String, Object> err = Map.of("resmsg", "게시글 수정에 실패했습니다",
                                             "resvalue", e.getMessage() );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
       }

    }

    // 게시글 삭제 - 현재 세션 기반 유저 검사 
    @DeleteMapping("/post/{id}")
    @Operation(summary = "게시글 삭제", description = "본인 게시글을 삭제합니다.")
    public ResponseEntity<?> deletePost(@PathVariable int id, HttpSession session) throws Exception{
        try {
        	User loginUser = (User) session.getAttribute("loginUser");
        	if(loginUser.getId() == id) {
        		boardService.deletePost(id);
                Map<String, Object> map = Map.of("resmsg", "게시글이 삭제되었습니다.", "resvalue", id);
                return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(map);
        	}else {
        		Map<String, Object> err = Map.of("resmsg", "게시글 삭제에 실패했습니다.", "resvalue", "본인이 작성한 게시글만 삭제가 가능합니다.");
        		return ResponseEntity
        				.status(HttpStatus.FORBIDDEN).body(err);
        	}
            
           
        } catch (RuntimeException e) {
                Map<String, Object> err = Map.of("resmsg", "게시글 삭제에 실패했습니다",
                                                 "resvalue", e.getMessage() );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }
    }


}
