package com.mayonedev.battle.domain.auth.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRefreshTokenDTO {
    private Long userId;
    private String refreshToken;
    private LocalDateTime expiresAt;
    private boolean revoked; // Keeping it for now if used elsewhere, but service ignores it.
}
