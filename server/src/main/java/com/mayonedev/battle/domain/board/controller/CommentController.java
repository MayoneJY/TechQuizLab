package com.mayonedev.battle.domain.board.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.mayonedev.battle.domain.board.dto.CommentDto;
import com.mayonedev.battle.domain.board.entity.Comment;
import com.mayonedev.battle.domain.board.service.CommentService;
import com.mayonedev.battle.domain.user.dto.UserDetailsDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/boards/{boardId}/post/{postId}/comment")
@Slf4j
@Tag(name = "Comment", description = "댓글 관리 API")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping
    @Operation(summary = "댓글 목록 조회", description = "특정 게시글의 댓글 목록을 조회합니다.")
    public ResponseEntity<?> getComments(
            @PathVariable("boardId") Integer boardId,
            @PathVariable("postId") Long postId) {

        try {
            List<CommentDto> comments = commentService.getCommentsByPost(boardId, postId);
            Map<String, Object> map = Map.of("resmsg", "댓글 목록 조회", "resvalue", comments);
            return ResponseEntity.ok(map);
        } catch (Exception e) {
            Map<String, Object> map = Map.of("resmsg", "댓글 목록 조회 실패", "resvalue", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
        }
    }

    @PostMapping
    @Operation(summary = "댓글 등록", description = "특정 게시글에 댓글을 등록합니다.")
    public ResponseEntity<?> createComment(
            @PathVariable("boardId") Integer boardId,
            @PathVariable("postId") Long postId,
            @RequestBody Comment comment, @AuthenticationPrincipal UserDetailsDTO loginuser) {

        try {
            comment.setBoardId(boardId);
            comment.setPostId(postId);
            comment.setUserId(loginuser.getUserId());

            commentService.createComment(comment);

            Map<String, Object> map = Map.of("resmsg", "댓글이 등록되었습니다.", "resvalue", comment);
            return ResponseEntity.status(HttpStatus.CREATED).body(map);
        } catch (Exception e) {
            Map<String, Object> map = Map.of("resmsg", "댓글 등록에 실패했습니다.", "resvalue", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
        }
    }

    @PostMapping("/{parentCommentId}/reply")
    @Operation(summary = "대댓글 등록", description = "특정 댓글에 대댓글을 등록합니다.")
    public ResponseEntity<?> createReply(
            @PathVariable("boardId") Integer boardId,
            @PathVariable("postId") Long postId,
            @PathVariable("parentCommentId") Long parentCommentId,
            @RequestBody Comment comment, @AuthenticationPrincipal UserDetailsDTO loginuser) {

        try {
            comment.setBoardId(boardId);
            comment.setPostId(postId);
            comment.setUserId(loginuser.getUserId());
            comment.setParentCommentId(parentCommentId);

            commentService.createComment(comment);

            Map<String, Object> map = Map.of("resmsg", "대댓글이 등록되었습니다.", "resvalue", comment);
            return ResponseEntity.status(HttpStatus.CREATED).body(map);
        } catch (Exception e) {
            Map<String, Object> map = Map.of("resmsg", "대댓글 등록에 실패했습니다.", "resvalue", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
        }
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다.")
    public ResponseEntity<?> deleteComment(
            @PathVariable("boardId") Integer boardId,
            @PathVariable("postId") Long postId,
            @PathVariable("commentId") Long commentId, @AuthenticationPrincipal UserDetailsDTO loginuser) {

        try {

            Comment comment = commentService.selectByCommentId(boardId, postId, commentId);

            if (!Long.valueOf(loginuser.getUserId()).equals(comment.getUserId())) {
                Map<String, Object> map = Map.of("resmsg", "댓글 삭제에 실패했습니다.", "resvalue", "삭제 권한이 없습니다.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(map);
            }

            commentService.deleteComment(commentId);

            Map<String, Object> map = Map.of("resmsg", "댓글이 삭제되었습니다.", "resvalue", commentId);
            return ResponseEntity.ok(map);
        } catch (Exception e) {
            Map<String, Object> map = Map.of("resmsg", "댓글 삭제에 실패했습니다.", "resvalue", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
        }
    }

}
