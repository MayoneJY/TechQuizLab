package com.mayonedev.battle.domain.battle.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkPractice {
    private Long userId;
    private Long bookmarkId;
    private Long practiceId;
    private String userAnswer;
    private String aiFeedbackGood;
    private String aiFeedbackBad;
    private Integer damage;
    private LocalDateTime createdAt;

    // Display fields from join
    private String questionText;
    private String difficulty;
    private String keywordTags;
}
