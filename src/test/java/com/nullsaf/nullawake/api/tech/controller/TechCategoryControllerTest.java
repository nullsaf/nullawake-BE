package com.nullsaf.nullawake.api.tech.controller;

import com.nullsaf.nullawake.api.auth.security.CustomUserDetails;
import com.nullsaf.nullawake.api.auth.service.JwtTokenProvider;
import com.nullsaf.nullawake.api.tech.dto.TechCategoryResponse;
import com.nullsaf.nullawake.api.tech.dto.TechCategoryResponse.TechCategoryDto;
import com.nullsaf.nullawake.api.tech.dto.TechStackListResponse;
import com.nullsaf.nullawake.api.tech.service.TechCategoryService;
import com.nullsaf.nullawake.common.exception.CustomException;
import com.nullsaf.nullawake.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WithMockUser
@WebMvcTest(TechCategoryController.class)
class TechCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TechCategoryService techCategoryService;

    // security 검증 제외하기 위해 mock으로
    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("기술 카테고리 목록 조회 성공")
    void getTechCategories_success() throws Exception {

        // given
        List<TechCategoryDto> categoryList = List.of(
            new TechCategoryDto(
                1L,
                "Backend",
                3L
            ),
            new TechCategoryDto(
                2L,
                "Frontend",
                2L
            )
        );

        TechCategoryResponse response =
            new TechCategoryResponse(categoryList);

        given(techCategoryService.getTechCategories())
            .willReturn(response);

        // when & then
        mockMvc.perform(get("/api/tech-categories"))
            .andExpect(status().isOk())

            // ApiResponse 검증
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message")
                .value("기술 카테고리 목록 조회에 성공했습니다."))

            // data 검증
            .andExpect(jsonPath("$.data.techCategoryDtoList").isArray())
            .andExpect(jsonPath("$.data.techCategoryDtoList.length()").value(2))

            // 첫 번째 요소 값 검증
            .andExpect(jsonPath("$.data.techCategoryDtoList[0].techCategoryId").value(1L))
            .andExpect(jsonPath("$.data.techCategoryDtoList[0].techCategoryName").value("Backend"))
            .andExpect(jsonPath("$.data.techCategoryDtoList[0].stackCount").value(3))

            // 두 번째 요소 값 검증
            .andExpect(jsonPath("$.data.techCategoryDtoList[1].techCategoryId").value(2L))
            .andExpect(jsonPath("$.data.techCategoryDtoList[1].techCategoryName").value("Frontend"))
            .andExpect(jsonPath("$.data.techCategoryDtoList[1].stackCount").value(2));

        verify(techCategoryService, times(1))
            .getTechCategories();
    }

    @Test
    @DisplayName("기술 카테고리 조회 실패 시 500 응답을 반환한다")
    void getTechCategories_queryFailed() throws Exception {
        // given
        given(techCategoryService.getTechCategories())
            .willThrow(new CustomException(ErrorCode.TECH_CATEGORY_QUERY_FAILED));

        // when & then
        mockMvc.perform(get("/api/tech-categories"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message")
                .value(ErrorCode.TECH_CATEGORY_QUERY_FAILED.getMessage()))
            .andExpect(jsonPath("$.data").doesNotExist());

        verify(techCategoryService, times(1))
            .getTechCategories();
    }

    @Test
    @DisplayName("카테고리별 기술 스택 목록 조회 성공")
    void getTechStacksByCategory_success() throws Exception {
        // given
        Long userId = 1L;
        Long categoryId = 1L;

        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        given(userDetails.getUserId()).willReturn(userId);

        TechStackListResponse response = new TechStackListResponse(
            1L,
            "Backend",
            List.of(
                new TechStackListResponse.TechStack(1L, "SpringBoot", "Java 기반 웹 애플리케이션", true),
                new TechStackListResponse.TechStack(2L, "Redis", "인메모리 데이터 저장소", false)
            )
        );

        given(techCategoryService.getTechStacksByCategory(userId, categoryId))
            .willReturn(response);

        // when & then
        mockMvc.perform(get("/api/tech-categories/{categoryId}/tech-stacks", categoryId)
                .with(authentication(
                    new UsernamePasswordAuthenticationToken(userDetails, null, List.of())
                )))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("기술 스택 목록 조회에 성공했습니다."))
            .andExpect(jsonPath("$.data.techCategoryId").value(1L))
            .andExpect(jsonPath("$.data.techCategoryName").value("Backend"))
            .andExpect(jsonPath("$.data.techStackList.length()").value(2))
            .andExpect(jsonPath("$.data.techStackList[0].techStackId").value(1L))
            .andExpect(jsonPath("$.data.techStackList[0].techStackName").value("SpringBoot"))
            .andExpect(jsonPath("$.data.techStackList[0].description").value("Java 기반 웹 애플리케이션"))
            .andExpect(jsonPath("$.data.techStackList[0].selected").value(true))
            .andExpect(jsonPath("$.data.techStackList[1].techStackId").value(2L))
            .andExpect(jsonPath("$.data.techStackList[1].techStackName").value("Redis"))
            .andExpect(jsonPath("$.data.techStackList[1].selected").value(false));

        verify(techCategoryService, times(1))
            .getTechStacksByCategory(userId, categoryId);
    }

    @Test
    @DisplayName("존재하지 않는 카테고리이면 404 응답을 반환한다")
    void getTechStacksByCategory_categoryNotFound() throws Exception {
        // given
        Long userId = 1L;
        Long categoryId = 999L;

        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        given(userDetails.getUserId()).willReturn(userId);

        given(techCategoryService.getTechStacksByCategory(userId, categoryId))
            .willThrow(new CustomException(ErrorCode.TECH_CATEGORY_NOT_FOUND));

        // when & then
        mockMvc.perform(get("/api/tech-categories/{categoryId}/tech-stacks", categoryId)
                .with(authentication(
                    new UsernamePasswordAuthenticationToken(userDetails, null, List.of())
                )))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value(ErrorCode.TECH_CATEGORY_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("기술 스택 조회 실패 시 500 응답을 반환한다")
    void getTechStacksByCategory_queryFailed() throws Exception {
        // given
        Long userId = 1L;
        Long categoryId = 1L;

        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        given(userDetails.getUserId()).willReturn(userId);

        given(techCategoryService.getTechStacksByCategory(userId, categoryId))
            .willThrow(new CustomException(ErrorCode.TECH_STACK_QUERY_FAILED));

        // when & then
        mockMvc.perform(get("/api/tech-categories/{categoryId}/tech-stacks", categoryId)
                .with(authentication(
                    new UsernamePasswordAuthenticationToken(userDetails, null, List.of())
                )))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value(ErrorCode.TECH_STACK_QUERY_FAILED.getMessage()));
    }
}