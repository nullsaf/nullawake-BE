package com.nullsaf.nullawake.api.tech.repository.techcategory;

import com.nullsaf.nullawake.api.tech.dto.TechCategoryResponse.TechCategoryDto;
import com.nullsaf.nullawake.api.tech.dto.TechStackListResponse;
import java.util.List;
import java.util.Optional;

/**
 * The interface Tech category repository custom.
 */
public interface TechCategoryRepositoryCustom {

    // 활성화된 카테고리를 스택 개수와 함께 조회
    List<TechCategoryDto> findActiveCategoriesWithStackCount();

    // 카테고리 별 기술 스택을 사용자의 선택 여부와 함께 조회
    Optional<TechStackListResponse> findTechStacksByCategoryIdAndUserId(Long techCategoryId, Long userId);
}
