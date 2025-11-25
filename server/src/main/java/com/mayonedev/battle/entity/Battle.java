package com.mayonedev.battle.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Battle {
    private Long id;
    private Long stageId;
    private String mode;
    private String status;
    private Integer difficulty;
    private LocalDateTime createdAt;
    private LocalDateTime closedAt;
}
