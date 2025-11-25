package com.mayonedev.battle.controller;

import com.mayonedev.battle.entity.Question;
import com.mayonedev.battle.entity.QuestionBookmark;
import com.mayonedev.battle.entity.UserQuestionHistory;
import com.mayonedev.battle.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
@Tag(name = "Question", description = "문제 풀이 및 오답 노트 API")
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping
    @Operation(summary = "주제별 문제 조회")
    public ResponseEntity<List<Question>> getQuestionsByTopic(@RequestParam Long topicId) {
        return ResponseEntity.ok(questionService.getQuestionsByTopic(topicId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "문제 상세 조회")
    public ResponseEntity<Question> getQuestionById(@PathVariable Long id) {
        return ResponseEntity.ok(questionService.getQuestionById(id));
    }

    @PostMapping("/{id}/submit")
    @Operation(summary = "정답 제출 (채점)")
    public ResponseEntity<Boolean> submitAnswer(
            @PathVariable Long id,
            @RequestBody SubmitAnswerRequest request) {
        boolean isCorrect = questionService.submitAnswer(request.getUserId(), id, request.getAnswer());
        return ResponseEntity.ok(isCorrect);
    }

    @PostMapping("/{id}/bookmark")
    @Operation(summary = "오답 노트(북마크) 추가")
    public ResponseEntity<Void> bookmarkQuestion(
            @PathVariable Long id,
            @RequestBody BookmarkRequest request) {
        questionService.bookmarkQuestion(request.getUserId(), id, request.getMemo());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/bookmarks")
    @Operation(summary = "나의 오답 노트 조회")
    public ResponseEntity<List<QuestionBookmark>> getBookmarks(@RequestParam Long userId) {
        return ResponseEntity.ok(questionService.getBookmarks(userId));
    }

    @GetMapping("/history")
    @Operation(summary = "나의 문제 풀이 기록 조회")
    public ResponseEntity<List<UserQuestionHistory>> getHistory(@RequestParam Long userId) {
        return ResponseEntity.ok(questionService.getHistory(userId));
    }

    @Data
    public static class SubmitAnswerRequest {
        private Long userId;
        private String answer;
    }

    @Data
    public static class BookmarkRequest {
        private Long userId;
        private String memo;
    }
}
