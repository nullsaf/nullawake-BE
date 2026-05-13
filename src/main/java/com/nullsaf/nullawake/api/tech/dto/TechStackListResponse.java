package com.nullsaf.nullawake.api.tech.dto;

import java.util.List;

public record TechStackListResponse(
    Long techCategoryId,
    String techCategoryName,
    List<TechStack> techStackList
) {
    public record TechStack(
        Long techStackId,
        String techStackName,
        String description,
        Boolean selected
    ) {
    }
}