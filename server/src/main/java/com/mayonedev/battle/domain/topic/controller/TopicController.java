package com.mayonedev.battle.domain.topic.controller;

import com.mayonedev.battle.domain.topic.entity.Topic;
import com.mayonedev.battle.domain.topic.entity.UserTopicLevel;
import com.mayonedev.battle.domain.topic.service.TopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
@Tag(name = "Topic", description = "CS 주제 및 레벨 관리 API")
public class TopicController {

    private final TopicService topicService;

    @GetMapping
    @Operation(summary = "주제 목록 조회", description = "모든 CS 주제 목록을 조회합니다.")
    public ResponseEntity<List<Topic>> getAllTopics() {
        return ResponseEntity.ok(topicService.getAllTopics());
    }

    @GetMapping("/{id}")
    @Operation(summary = "주제 상세 조회")
    public ResponseEntity<Topic> getTopicById(@PathVariable Long id) {
        return ResponseEntity.ok(topicService.getTopicById(id));
    }

    @GetMapping("/{topicId}/level")
    @Operation(summary = "나의 주제별 레벨 조회")
    public ResponseEntity<UserTopicLevel> getUserTopicLevel(
            @PathVariable Long topicId,
            @RequestParam Long userId) {
        return ResponseEntity.ok(topicService.getUserTopicLevel(userId, topicId));
    }
}
