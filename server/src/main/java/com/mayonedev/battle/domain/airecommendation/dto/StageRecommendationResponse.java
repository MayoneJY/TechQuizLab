package com.mayonedev.battle.domain.airecommendation.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Data
public class StageRecommendationResponse {
    @NotNull
    private Long userId;
    
    private List<String> queryKeywords;
    private List<RecommendedStage> recommendedStages;
    
    @Data
    public static class RecommendedStage {
        private Long stageId;
        private String title;
        private String companyName;
        private String jobCategory;
        private Double score;
        private List<Map<String, Object>> evidence;
    }
}

