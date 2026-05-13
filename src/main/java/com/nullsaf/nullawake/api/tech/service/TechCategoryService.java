package com.nullsaf.nullawake.api.tech.service;

import com.nullsaf.nullawake.api.tech.dto.TechCategoryResponse;
import com.nullsaf.nullawake.api.tech.repository.TechCategoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 카테고리 관련 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TechCategoryService {

    private final TechCategoryRepository techCategoryRepository;

    /**
     * 전체 카테고리 조회
     * 카테고리명, 각 카테고리에 포함된 스택의 개수를 조회한다,
     * @return 카테고리 리스트
     */
    @Transactional(readOnly = true)
    public TechCategoryResponse getTechCategories() {
        List<TechCategoryResponse.TechCategory> techCategoryList =
            techCategoryRepository.findActiveCategoriesWithStackCount();

        return new TechCategoryResponse(techCategoryList);
    }
}
