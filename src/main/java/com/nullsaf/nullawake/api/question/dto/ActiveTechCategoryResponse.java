package com.nullsaf.nullawake.api.question.dto;

import java.util.List;

public record ActiveTechCategoryResponse(
    List<ActiveTechCategory> techCategoryList
) {
    public record ActiveTechCategory(
        Long techCategoryId,
        String techCategoryName
    ) {}
}
