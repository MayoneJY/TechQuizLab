package com.mayonedev.battle.dto;

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
    private LocalDateTime validity;
    private boolean revoked;
}
