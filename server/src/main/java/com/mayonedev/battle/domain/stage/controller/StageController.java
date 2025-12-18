package com.mayonedev.battle.domain.stage.controller;

import com.mayonedev.battle.domain.stage.entity.Stage;
import com.mayonedev.battle.domain.stage.service.StageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stages")
@RequiredArgsConstructor
@Tag(name = "Stage", description = "채용 공고(스테이지) API")
public class StageController {

    private final StageService stageService;

    @GetMapping
    @Operation(summary = "스테이지 목록 조회", 
               description = "스테이지를 직무별로 조회합니다.")
    public ResponseEntity<Map<String, Object>> getStages(
            @RequestParam(required = false) String jobCategories,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        
        List<String> jobCategoryList = null;
        if (jobCategories != null && !jobCategories.isEmpty()) {
            jobCategoryList = Arrays.stream(jobCategories.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }

        Map<String, Object> result = stageService.getStagesWithPaging(
                jobCategoryList, page, size);
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{stageId}")
    @Operation(summary = "스테이지 상세 조회")
    public ResponseEntity<Stage> getStageById(@PathVariable Long stageId) {
        return ResponseEntity.ok(stageService.getStageById(stageId));
    }
}
