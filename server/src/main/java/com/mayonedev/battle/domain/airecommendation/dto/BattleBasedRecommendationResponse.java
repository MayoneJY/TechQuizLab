package com.mayonedev.battle.domain.airecommendation.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class BattleBasedRecommendationResponse {
    private String snapshotId;
    private Long userId;
    private List<Map<String, Object>> tagMappings;
    private Map<String, Double> currentScores;
    private WeaknessRecommendation weaknessRecommendation;
    private List<Map<String, Object>> associationRecommendations;
    private Map<String, Object> battleSummary;
}

@Data
class WeaknessRecommendation {
    private List<WeakKeywordResponse> weakKeywords;
    private List<LearningPlanItem> learningPlan;
}

@Data
class WeakKeywordResponse {
    private String keyword;
    private String axis;
    private Double score;
    private String reasonCode;
    private String reasonText;
    private List<Map<String, Object>> evidence;
}

@Data
class LearningPlanItem {
    private List<String> keywords;
    private String axis;
    private String reasonCode;
    private String reasonText;
    private List<Map<String, Object>> evidence;
}
