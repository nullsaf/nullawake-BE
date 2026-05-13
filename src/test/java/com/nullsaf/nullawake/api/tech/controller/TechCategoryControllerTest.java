package com.nullsaf.nullawake.api.tech.controller;

import com.nullsaf.nullawake.api.auth.service.JwtTokenProvider;
import com.nullsaf.nullawake.api.tech.dto.TechCategoryResponse;
import com.nullsaf.nullawake.api.tech.service.TechCategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
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
        List<TechCategoryResponse.TechCategory> categoryList = List.of(
            new TechCategoryResponse.TechCategory(
                1L,
                "Backend",
                3L
            ),
            new TechCategoryResponse.TechCategory(
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
            .andExpect(jsonPath("$.data.techCategoryList").isArray())
            .andExpect(jsonPath("$.data.techCategoryList.length()").value(2))

            // 첫 번째 요소 값 검증
            .andExpect(jsonPath("$.data.techCategoryList[0].techCategoryId")
                .value(1L))
            .andExpect(jsonPath("$.data.techCategoryList[0].techCategoryName")
                .value("Backend"))
            .andExpect(jsonPath("$.data.techCategoryList[0].stackCount")
                .value(3))

            // 두 번째 요소 값 검증
            .andExpect(jsonPath("$.data.techCategoryList[1].techCategoryId")
                .value(2L))
            .andExpect(jsonPath("$.data.techCategoryList[1].techCategoryName")
                .value("Frontend"))
            .andExpect(jsonPath("$.data.techCategoryList[1].stackCount")
                .value(2));

        verify(techCategoryService, times(1))
            .getTechCategories();
    }
}