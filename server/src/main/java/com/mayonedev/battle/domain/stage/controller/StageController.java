package com.mayonedev.battle.domain.stage.controller;

import com.mayonedev.battle.domain.stage.entity.Stage;
import com.mayonedev.battle.domain.stage.service.StageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stages")
@RequiredArgsConstructor
@Tag(name = "Stage", description = "채용 공고(스테이지) API")
public class StageController {

    private final StageService stageService;

    @GetMapping
    @Operation(summary = "전체 스테이지 목록 조회")
    public ResponseEntity<List<Stage>> getAllStages() {
        return ResponseEntity.ok(stageService.getAllStages());
    }

    @GetMapping("/{stageId}")
    @Operation(summary = "스테이지 상세 조회")
    public ResponseEntity<Stage> getStageById(@PathVariable Long stageId) {
        return ResponseEntity.ok(stageService.getStageById(stageId));
    }
}
