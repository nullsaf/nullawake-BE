package com.nullsaf.nullawake.api.tech.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.nullsaf.nullawake.api.tech.dto.TechCategoryResponse;
import com.nullsaf.nullawake.api.tech.dto.TechCategoryResponse.TechCategory;
import com.nullsaf.nullawake.api.tech.repository.TechCategoryRepository;
import com.nullsaf.nullawake.common.exception.CustomException;
import com.nullsaf.nullawake.common.exception.ErrorCode;
import java.util.List;
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
        List<TechCategory> categoryList = List.of(
            new TechCategoryResponse.TechCategory(1L, "Backend", 3L),
            new TechCategoryResponse.TechCategory(2L, "Frontend", 2L)
        );

        given(techCategoryRepository.findActiveCategoriesWithStackCount())
            .willReturn(categoryList);

        // when
        TechCategoryResponse response = techCategoryService.getTechCategories();

        // then
        assertThat(response).isNotNull();
        assertThat(response.getTechCategoryList()).hasSize(2);

        assertThat(response.getTechCategoryList().get(0).getTechCategoryId()).isEqualTo(1L);
        assertThat(response.getTechCategoryList().get(0).getTechCategoryName()).isEqualTo("Backend");
        assertThat(response.getTechCategoryList().get(0).getStackCount()).isEqualTo(3L);

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
}