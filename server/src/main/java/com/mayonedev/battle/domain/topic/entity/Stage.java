package com.mayonedev.battle.domain.topic.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stage {
    private Long stageId;
    private String companyName;
    private String title;
    private String jobCategory;
    private String content;
    private LocalDateTime deadline;
}
