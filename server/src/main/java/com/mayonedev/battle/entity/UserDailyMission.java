package com.mayonedev.battle.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDailyMission {
    private Long id;
    private Long userId;
    private Long dailyMissionId;
    private Integer progress;
    private Boolean isCompleted;
    private Boolean isClaimed;
    private LocalDate assignedDate;
}
