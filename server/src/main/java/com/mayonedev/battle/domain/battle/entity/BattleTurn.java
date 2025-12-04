package com.mayonedev.battle.domain.battle.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BattleTurn {
    private Long id;
    private Long battleId;
    private Integer turnNo;
    private Long attackerId;
    private String questionText;
    private String answerText;
    private Float multiplier;
    private Integer damage;
    private LocalDateTime createdAt;
}
