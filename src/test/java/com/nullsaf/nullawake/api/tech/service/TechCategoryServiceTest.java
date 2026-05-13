package com.nullsaf.nullawake.api.tech.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.nullsaf.nullawake.api.tech.dto.TechCategoryResponse;
import com.nullsaf.nullawake.api.tech.dto.TechCategoryResponse.TechCategoryDto;
import com.nullsaf.nullawake.api.tech.dto.TechStackListResponse;
import com.nullsaf.nullawake.api.tech.repository.TechCategoryRepository;
import com.nullsaf.nullawake.common.exception.CustomException;
import com.nullsaf.nullawake.common.exception.ErrorCode;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TechCategoryServiceTest {

    @Mock
    private TechCategoryRepository techCategoryRepository;

    @InjectMocks
    private TechCategoryService techCategoryService;

    @Test
    @DisplayName("활성 기술 카테고리 목록과 스택 개수를 조회한다")
    void getTechCategories_success() {
        // given
        List<TechCategoryDto> categoryList = List.of(
            new TechCategoryDto(1L, "Backend", 3L),
            new TechCategoryDto(2L, "Frontend", 2L)
        );

        given(techCategoryRepository.findActiveCategoriesWithStackCount())
            .willReturn(categoryList);

        // when
        TechCategoryResponse response = techCategoryService.getTechCategories();

        // then
        assertThat(response).isNotNull();
        assertThat(response.getTechCategoryDtoList()).hasSize(2);

        assertThat(response.getTechCategoryDtoList().get(0).getTechCategoryId()).isEqualTo(1L);
        assertThat(response.getTechCategoryDtoList().get(0).getTechCategoryName()).isEqualTo("Backend");
        assertThat(response.getTechCategoryDtoList().get(0).getStackCount()).isEqualTo(3L);

        verify(techCategoryRepository, times(1))
            .findActiveCategoriesWithStackCount();
    }

    @Test
    @DisplayName("기술 카테고리 조회 중 Repository 예외가 발생하면 CustomException을 던진다")
    void getTechCategories_queryFailed() {
        // given
        given(techCategoryRepository.findActiveCategoriesWithStackCount())
            .willThrow(new RuntimeException("DB error"));

        // when & then
        assertThatThrownBy(() -> techCategoryService.getTechCategories())
            .isInstanceOf(CustomException.class)
            .hasMessage(ErrorCode.TECH_CATEGORY_QUERY_FAILED.getMessage())
            .extracting("errorCode")
            .isEqualTo(ErrorCode.TECH_CATEGORY_QUERY_FAILED);

        verify(techCategoryRepository, times(1))
            .findActiveCategoriesWithStackCount();
    }

    @Test
    @DisplayName("카테고리별 기술 스택 목록 조회 성공")
    void getTechStacksByCategory_success() {
        // given
        Long userId = 1L;
        Long categoryId = 1L;

        TechStackListResponse response = new TechStackListResponse(
            1L,
            "Backend",
            List.of(
                new TechStackListResponse.TechStack(1L, "SpringBoot", "Java 기반 웹 애플리케이션", true),
                new TechStackListResponse.TechStack(2L, "Redis", "인메모리 데이터 저장소", false)
            )
        );

        given(techCategoryRepository.findTechStacksByCategoryIdAndUserId(categoryId, userId))
            .willReturn(Optional.of(response));

        // when
        TechStackListResponse result =
            techCategoryService.getTechStacksByCategory(userId, categoryId);

        // then
        assertThat(result.techCategoryId()).isEqualTo(1L);
        assertThat(result.techCategoryName()).isEqualTo("Backend");
        assertThat(result.techStackList()).hasSize(2);
        assertThat(result.techStackList().get(0).selected()).isTrue();
        assertThat(result.techStackList().get(1).selected()).isFalse();

        verify(techCategoryRepository, times(1))
            .findTechStacksByCategoryIdAndUserId(categoryId, userId);
    }

    @Test
    @DisplayName("존재하지 않는 카테고리이면 TECH_CATEGORY_NOT_FOUND 예외를 던진다")
    void getTechStacksByCategory_categoryNotFound() {
        // given
        Long userId = 1L;
        Long categoryId = 999L;

        given(techCategoryRepository.findTechStacksByCategoryIdAndUserId(categoryId, userId))
            .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
            techCategoryService.getTechStacksByCategory(userId, categoryId)
        )
            .isInstanceOf(CustomException.class)
            .hasMessage(ErrorCode.TECH_CATEGORY_NOT_FOUND.getMessage())
            .extracting("errorCode")
            .isEqualTo(ErrorCode.TECH_CATEGORY_NOT_FOUND);
    }

    @Test
    @DisplayName("기술 스택 조회 중 Repository 예외가 발생하면 TECH_STACK_QUERY_FAILED 예외를 던진다")
    void getTechStacksByCategory_queryFailed() {
        // given
        Long userId = 1L;
        Long categoryId = 1L;

        given(techCategoryRepository.findTechStacksByCategoryIdAndUserId(categoryId, userId))
            .willThrow(new RuntimeException("DB error"));

        // when & then
        assertThatThrownBy(() ->
            techCategoryService.getTechStacksByCategory(userId, categoryId)
        )
            .isInstanceOf(CustomException.class)
            .hasMessage(ErrorCode.TECH_STACK_QUERY_FAILED.getMessage())
            .extracting("errorCode")
            .isEqualTo(ErrorCode.TECH_STACK_QUERY_FAILED);
    }
}