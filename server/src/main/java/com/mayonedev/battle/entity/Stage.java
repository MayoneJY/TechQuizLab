package com.mayonedev.battle.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stage {
    private Long id;
    private String url;
    private Boolean active;
    private String name;
    private String title;
    private String industryName;
    private String jobTypeName;
    private String experienceLevelName;
    private String requiredEducationLevelName;
    private LocalDateTime openingTimestamp;
    private LocalDateTime expirationTimestamp;
    private String keyword;
    private String locationName;
}
