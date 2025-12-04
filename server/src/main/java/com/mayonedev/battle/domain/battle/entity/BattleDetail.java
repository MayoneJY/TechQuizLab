package com.mayonedev.battle.domain.battle.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BattleDetail {
    private Long userId;
    private Long battleId;
    private Long detailId;
    private String questionText;
    private String keywordTags;
    private String difficulty;
    private String userAnswer;
    private String aiFeedback;
    private Integer damage;
    private LocalDateTime createdAt;
}
