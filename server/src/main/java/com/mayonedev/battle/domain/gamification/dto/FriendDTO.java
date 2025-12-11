package com.mayonedev.battle.domain.gamification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendDTO {
    private Long userId;
    private String nickname;
    private Integer level;
    private Long exp;
    private Integer solvedCount;
    private Boolean isRival;
}
