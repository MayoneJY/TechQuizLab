package com.mayonedev.battle.domain.airecommendation.dto;

import lombok.Data;
import java.util.Map;

@Data
public class BattleBasedRecommendationRequest {
    private Long userId;
    private Map<String, Double> requiredScores;
    private Map<String, Object> stage;
    private Integer limit;
    private String snapshotId;
}

