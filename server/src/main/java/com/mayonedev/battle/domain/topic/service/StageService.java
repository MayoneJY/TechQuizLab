package com.mayonedev.battle.domain.topic.service;

import com.mayonedev.battle.domain.topic.entity.Stage;
import java.util.List;

public interface StageService {
    List<Stage> getAllStages();

    Stage getStageById(Long stageId);
}
