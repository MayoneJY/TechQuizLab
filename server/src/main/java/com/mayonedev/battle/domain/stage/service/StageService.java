package com.mayonedev.battle.domain.stage.service;

import com.mayonedev.battle.domain.stage.dao.StageDao;
import com.mayonedev.battle.domain.stage.entity.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StageService {
    private final StageDao stageDao;

    public List<Stage> getAllStages() {
        return stageDao.findAll();
    }

    public Stage getStageById(Long stageId) {
        return stageDao.findById(stageId)
                .orElseThrow(() -> new RuntimeException("Stage not found with id: " + stageId));
    }
}
