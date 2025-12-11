package com.mayonedev.battle.domain.topic.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTopicLevel {
    private Long id;
    private Long userId;
    private Long topicId;
    private Integer level;
    private Integer exp;
}
