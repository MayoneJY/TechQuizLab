package com.mayonedev.battle.domain.gamification.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDailyMission {
    private Long userId; // PK
    private LocalDate missionDate; // PK
    private Integer missionId; // PK

    private Integer currentCount;
    private Boolean isCompleted;
    private Boolean isRewarded;
    private LocalDateTime createdAt;

    // Joined field
    private DailyMission mission;
}
