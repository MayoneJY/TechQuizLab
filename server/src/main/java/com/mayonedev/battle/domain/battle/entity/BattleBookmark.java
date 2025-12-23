package com.mayonedev.battle.domain.battle.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BattleBookmark {
    private Long userId;
    private Long bookmarkId;
    private Long refBattleId;
    private Long refDetailId;
    private String memo;
    private LocalDateTime createdAt;

    // Display fields
    private String questionText;
    private String userAnswer;
    private String aiFeedbackGood;
    private String aiFeedbackBad;
    private Integer damage;
    private String difficulty;
    private String keywordTags;
    private String stageTitle;
    private String jobCategory;
}
