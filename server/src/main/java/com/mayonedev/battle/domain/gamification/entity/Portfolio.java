package com.mayonedev.battle.domain.gamification.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Portfolio {
    private Long userId;
    private Long pfId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
}
