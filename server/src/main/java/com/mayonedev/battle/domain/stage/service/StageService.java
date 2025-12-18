package com.mayonedev.battle.domain.stage.service;

import com.mayonedev.battle.domain.stage.dao.StageDao;
import com.mayonedev.battle.domain.stage.dto.stageDTO;
import com.mayonedev.battle.domain.stage.entity.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
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

    public stageDTO getStagesWithPaging(
            List<String> jobCategories,
            int page,
            int size
    ) {
        if (size <= 0) size = 12; // 방어 (원하면 제거 가능)
        if (page < 0) page = 0;

        Map<String, Object> params = new HashMap<>();
        int offset = page * size;
        params.put("offset", offset);
        params.put("size", size);

        List<Stage> content;
        long totalElements;

        if (jobCategories != null && !jobCategories.isEmpty()) {
            params.put("jobCategories", jobCategories);
            content = stageDao.findByJobCategoriesWithPaging(params);
            totalElements = stageDao.countByJobCategories(params);
        } else {
            content = stageDao.findAllWithPaging(params);
            totalElements = stageDao.countAll();
        }

        int totalPages = (int) Math.ceil((double) totalElements / size);

        return stageDTO.builder()
                .content(content)
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .jobCategories(jobCategories != null ? jobCategories : List.of())
                .build();
    }}
