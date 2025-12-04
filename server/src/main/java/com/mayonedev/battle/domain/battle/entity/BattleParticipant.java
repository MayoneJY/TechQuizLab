package com.mayonedev.battle.domain.battle.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BattleParticipant {
    private Long id;
    private Long battleId;
    private Long userId;
    private String role;
    private Integer score;
    private LocalDateTime joinedAt;
}
