package com.mayonedev.battle.domain.gamification.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyMission {
    private Integer missionId;
    private String title;
    private String missionType;
    private Integer goalCount;
    private Integer rewardExp;
    private Boolean isActive;
}
