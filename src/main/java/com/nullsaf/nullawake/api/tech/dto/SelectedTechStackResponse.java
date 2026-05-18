package com.nullsaf.nullawake.api.tech.dto;

import java.util.List;

public record SelectedTechStackResponse(
    List<TechCategoryInfo> techCategoryList
) {

    public record TechCategoryInfo(
        Long techCategoryId,
        String techCategoryName,
        Integer selectedTechStackCount,
        List<SelectedTechStackInfo> selectedTechStackList
    ) {
        public record SelectedTechStackInfo(
            Long techStackId,
            String techStackName
        ) {}
    }
}
