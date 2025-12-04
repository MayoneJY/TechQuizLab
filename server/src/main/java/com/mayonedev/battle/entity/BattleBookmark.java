package com.mayonedev.battle.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BattleBookmark {
    private Long userId;
    private Long bookmarkId;
    private Long refBattleId;
    private Long refDetailId;
    private String memo;
    private LocalDateTime createdAt;
}
