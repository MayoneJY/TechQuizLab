package com.mayonedev.battle.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyLog {
    private Long userId;
    private LocalDate logDate;
    private Boolean isAttended;
    private Integer dailyExp;
}
