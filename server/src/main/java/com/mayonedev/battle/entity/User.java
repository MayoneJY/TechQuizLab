package com.mayonedev.battle.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;                    // PK
    private String email;               // 로그인용 이메일
    private String password;            // 암호 (해시 저장)
    private String nickname;            // 닉네임
    private Integer level;              // 레벨
    private Integer exp;                // 경험치
    private LocalDateTime createdAt;    // 가입일
}