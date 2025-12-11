package com.mayonedev.battle.domain.user.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Friend {
    private Long userId;
    private Long friendId;
    private LocalDateTime createdAt;
}
