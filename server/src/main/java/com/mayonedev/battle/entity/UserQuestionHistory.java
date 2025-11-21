package com.mayonedev.battle.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserQuestionHistory {
    private Long id;
    private Long userId;
    private Long questionId;
    private Boolean isCorrect;
    private LocalDateTime createdAt;
}
