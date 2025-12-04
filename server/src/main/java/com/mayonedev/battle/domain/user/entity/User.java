package com.mayonedev.battle.domain.user.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long userId; // PK (user_id)
    private String email; // 로그인용 이메일
    private String password; // 암호 (해시 저장)
    private String nickname; // 닉네임
    private Integer level; // 레벨
    private Long exp; // 경험치 (bigint)
    private LocalDateTime createdAt; // 가입일
    private String role; // 권한
    private boolean enabled; // 활성화
    private Integer solvedCount; // 해결한 문제/배틀 수
    private Integer currentStreak; // 현재 연속 학습일
    private Integer maxStreak; // 최대 연속 학습일
    private LocalDateTime lastLoginAt; // 마지막 접속일
}