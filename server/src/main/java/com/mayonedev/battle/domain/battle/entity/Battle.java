package com.mayonedev.battle.domain.battle.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Battle {
    private Long userId;
    private Long battleId;
    private Long stageId;
    private Long pfId;
    private Integer totalDamage;
    private String status;
    private LocalDateTime createdAt;
    private String jobCategory;

    // Display fields
    private String stageTitle;
}
