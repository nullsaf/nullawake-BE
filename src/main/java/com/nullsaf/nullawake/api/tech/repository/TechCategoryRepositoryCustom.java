package com.nullsaf.nullawake.api.tech.repository;

import com.nullsaf.nullawake.api.tech.dto.TechCategoryResponse;
import java.util.List;

/**
 * The interface Tech category repository custom.
 */
public interface TechCategoryRepositoryCustom {

    /**
     * 활성화된 카테고리를 스택 개수와 함께 조회
     *
     * @return TechCategoryList
     */
    List<TechCategoryResponse.TechCategory> findActiveCategoriesWithStackCount();
}
