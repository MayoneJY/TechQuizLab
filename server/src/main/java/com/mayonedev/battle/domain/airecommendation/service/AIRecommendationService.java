package com.mayonedev.battle.domain.airecommendation.service;

import com.mayonedev.battle.domain.airecommendation.dto.LearningPlanResponse;
import com.mayonedev.battle.domain.airecommendation.dto.StageRecommendationResponse;

public interface AIRecommendationService {
    LearningPlanResponse getLearningPlan(Long userId, Integer limit);
    StageRecommendationResponse getStageRecommendations(Long userId, Integer limit, Integer stageLimit);
}

