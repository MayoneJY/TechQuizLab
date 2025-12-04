package com.mayonedev.battle.domain.gamification.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Achievement {
    private Long id;
    private String name;
    private String description;
    private String conditionType;
    private Integer conditionValue;
    private Integer rewardExp;
    private String imgUrl;
}
