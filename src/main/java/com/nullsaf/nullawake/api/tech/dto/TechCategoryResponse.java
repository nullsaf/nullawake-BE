package com.nullsaf.nullawake.api.tech.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TechCategoryResponse {

    private List<TechCategoryDto> techCategoryDtoList;

    @Getter
    @AllArgsConstructor
    public static class TechCategoryDto {

        private Long techCategoryId;
        private String techCategoryName;
        private Long stackCount;
    }
}