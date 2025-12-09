package com.mayonedev.battle.domain.stage.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stage {
    private Long stageId;
    private String companyName;
    private String title;
    private LocalDateTime deadline;
    private String jobCategory;
    private String content;
}
