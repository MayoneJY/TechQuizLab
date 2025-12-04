package com.mayonedev.battle.domain.topic.service;

import com.mayonedev.battle.domain.topic.dao.StageDao;
import com.mayonedev.battle.domain.topic.entity.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StageServiceImpl implements StageService {

    private final StageDao stageDao;

    @Override
    public List<Stage> getAllStages() {
        return stageDao.findAll();
    }

    @Override
    public Stage getStageById(Long stageId) {
        return stageDao.findById(stageId);
    }
}
