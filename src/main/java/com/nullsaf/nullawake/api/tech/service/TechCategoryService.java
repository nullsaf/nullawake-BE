package com.nullsaf.nullawake.api.tech.service;

import com.nullsaf.nullawake.api.tech.dto.TechCategoryResponse;
import com.nullsaf.nullawake.api.tech.dto.TechCategoryResponse.TechCategoryDto;
import com.nullsaf.nullawake.api.tech.dto.TechStackListResponse;
import com.nullsaf.nullawake.api.tech.repository.TechCategoryRepository;
import com.nullsaf.nullawake.common.exception.CustomException;
import com.nullsaf.nullawake.common.exception.ErrorCode;
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
     * 전체 카테고리 조회 카테고리명, 각 카테고리에 포함된 스택의 개수를 조회한다,
     *
     * @return 카테고리 리스트
     */
    @Transactional(readOnly = true)
    public TechCategoryResponse getTechCategories() {
        try {
            List<TechCategoryDto> techCategoryDtoList =
                techCategoryRepository.findActiveCategoriesWithStackCount();

            log.info("[TechCategoryService] 활성 기술 카테고리 목록 조회 완료 - categoryCount={}",
                techCategoryDtoList.size());

            return new TechCategoryResponse(techCategoryDtoList);
        } catch (Exception e) {
            log.error("[TechCategoryService] 기술 카테고리 조회 중 예외 발생", e);
            throw new CustomException(ErrorCode.TECH_CATEGORY_QUERY_FAILED);
        }
    }

    /**
     * 카테고리별 스택 리스트 조회
     *
     * @param userId
     * @param techCategoryId
     * @return 카테고리 별 스택 리스트
     */
    @Transactional(readOnly = true)
    public TechStackListResponse getTechStacksByCategory(Long userId, Long techCategoryId) {
        log.info("[TechCategoryService] 카테고리별 기술 스택 조회 시작 - userId={}, techCategoryId={}",
            userId, techCategoryId);

        try {
            TechStackListResponse response =
                techCategoryRepository.findTechStacksByCategoryIdAndUserId(techCategoryId, userId)
                    .orElseThrow(() -> new CustomException(ErrorCode.TECH_CATEGORY_NOT_FOUND));

            log.info("[TechCategoryService] 카테고리별 기술 스택 조회 완료 - userId={}, categoryId={}, stackCount={}",
                userId,
                techCategoryId,
                response.techStackList().size()
            );

            return response;

        } catch (CustomException e) {
            throw e;

        } catch (Exception e) {
            log.error("[TechCategoryService] 기술 스택 조회 중 예외 발생 - userId={}, categoryId={}",
                userId, techCategoryId, e);
            throw new CustomException(ErrorCode.TECH_STACK_QUERY_FAILED);
        }
    }
}
