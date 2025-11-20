package com.mayonedev.battle.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    private Long id;
    private Long topicId;
    private String content;
    private String answer;
    private Integer difficulty;
    private LocalDateTime createdAt;
}
