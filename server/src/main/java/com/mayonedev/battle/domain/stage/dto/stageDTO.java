package com.mayonedev.battle.domain.stage.dto;

import java.util.List;

import com.mayonedev.battle.domain.stage.entity.Stage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class stageDTO {
	
	    private final List<Stage> content;
	    private final String keyword;
        private final int page;
        private final int size;
        private final long totalElements;
		private final int totalPages;
        private final List<String> jobCategories;
}
