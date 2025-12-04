package com.mayonedev.battle.domain.gamification.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyMission {
    private Long id;
    private String content;
    private String missionType;
    private Integer targetValue;
    private Integer rewardExp;
}
