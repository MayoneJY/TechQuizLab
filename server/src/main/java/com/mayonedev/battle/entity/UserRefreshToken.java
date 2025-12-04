package com.mayonedev.battle.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRefreshToken {
    private Long id;
    private String refreshToken;
    private Long userId;
    private LocalDateTime validity;
    private boolean revoked;
}
