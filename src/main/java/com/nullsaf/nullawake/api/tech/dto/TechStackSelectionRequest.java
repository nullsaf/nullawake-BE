package com.nullsaf.nullawake.api.tech.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record TechStackSelectionRequest(

    @NotNull(message = "기술 스택 목록은 필수입니다.")
    @Valid
    List<TechStackSelection> techStackList

) {
    public record TechStackSelection(

        @NotNull(message = "기술 스택 ID는 필수입니다.")
        Long techStackId,

        @NotNull(message = "선택 여부는 필수입니다.")
        Boolean selected
    ) {
    }
}