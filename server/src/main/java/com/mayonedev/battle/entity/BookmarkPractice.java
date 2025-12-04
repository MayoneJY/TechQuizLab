package com.mayonedev.battle.entity;

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
    private String aiFeedback;
    private Integer damage;
    private LocalDateTime createdAt;
}
