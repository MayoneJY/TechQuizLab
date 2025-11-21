package com.mayonedev.battle.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionBookmark {
    private Long id;
    private Long userId;
    private Long questionId;
    private String memo;
    private LocalDateTime createdAt;
}
