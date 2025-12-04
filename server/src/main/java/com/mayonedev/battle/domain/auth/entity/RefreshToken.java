package com.mayonedev.battle.domain.auth.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    private Long userId;
    private Long tokenId;
    private String tokenValue;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
}
