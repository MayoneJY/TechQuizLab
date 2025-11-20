package com.mayonedev.battle.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAchievement {
    private Long id;
    private Long userId;
    private Long achievementId;
    private LocalDateTime achievedAt;
}
