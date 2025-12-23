package com.mayonedev.battle.domain.airecommendation.controller;

import com.mayonedev.battle.domain.airecommendation.constants.AIRecommendationConstants;
import com.mayonedev.battle.domain.airecommendation.dto.LearningPlanResponse;
import com.mayonedev.battle.domain.airecommendation.dto.StageRecommendationResponse;
import com.mayonedev.battle.domain.airecommendation.service.AIRecommendationService;
import com.mayonedev.battle.domain.user.dto.UserDetailsDTO;
import com.mayonedev.battle.exception.GlobalException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/recommendations")
@RequiredArgsConstructor
@Tag(name = "AI Recommendation", description = "AI 학습 추천 API")
public class AIRecommendationController {

    private final AIRecommendationService aiRecommendationService;

    @GetMapping("/learning-plan")
    @Operation(summary = "태그 기반 키워드 추천 (학습 계획 조회)")
    public ResponseEntity<LearningPlanResponse> getLearningPlan(
            @AuthenticationPrincipal UserDetailsDTO userDetails,
            @RequestParam(required = false, defaultValue = "200") Integer limit) {
        
        if (userDetails == null) {
            throw new GlobalException(HttpStatus.UNAUTHORIZED, "인증이 필요합니다.");
        }
        
        LearningPlanResponse response = aiRecommendationService.getLearningPlan(
                userDetails.getUserId(), 
                limit != null ? limit : AIRecommendationConstants.DEFAULT_LIMIT
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stage-recommendations")
    @Operation(summary = "공고 추천 (스테이지 추천 조회)")
    public ResponseEntity<StageRecommendationResponse> getStageRecommendations(
            @AuthenticationPrincipal UserDetailsDTO userDetails,
            @RequestParam(required = false, defaultValue = "200") Integer limit,
            @RequestParam(required = false, defaultValue = "300") Integer stageLimit) {
        
        if (userDetails == null) {
            throw new GlobalException(HttpStatus.UNAUTHORIZED, "인증이 필요합니다.");
        }
        
        StageRecommendationResponse response = aiRecommendationService.getStageRecommendations(
                userDetails.getUserId(),
                limit != null ? limit : AIRecommendationConstants.DEFAULT_LIMIT,
                stageLimit != null ? stageLimit : AIRecommendationConstants.DEFAULT_STAGE_LIMIT
        );
        return ResponseEntity.ok(response);
    }
}

