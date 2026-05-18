package com.nullsaf.nullawake.api.alarm.dto.response;

import java.util.List;

public record AlarmCategoryStackResponse(
        List<CategoryResponse> categoryList
) {
    public record CategoryResponse(
            Long categoryId,
            String categoryName,
            int selectedStackCount,
            List<StackResponse> selectedStackList
    ) {
    }

    public record StackResponse(
            Long stackId,
            String stackName
    ) {
    }
}
