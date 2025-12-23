package com.mayonedev.battle.domain.airecommendation.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Data
public class LearningPlanResponse {
    @NotNull
    private Long userId;
    
    private List<WeakKeywordDTO> weakKeywords;
    private List<LearningPlanDTO> learningPlan;
    
    @Data
    public static class WeakKeywordDTO {
        private String keyword;
        private String axis;
        private Double score;
        private String reasonCode;
        private String reasonText;
        private List<Map<String, Object>> evidence;
    }
    
    @Data
    public static class LearningPlanDTO {
        private List<String> keywords;
        private String axis;
        private String reasonCode;
        private String reasonText;
        private List<Map<String, Object>> evidence;
    }
}

