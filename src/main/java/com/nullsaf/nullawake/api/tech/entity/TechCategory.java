package com.nullsaf.nullawake.api.tech.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TECH_CATEGORY")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TechCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tech_category_id")
    @Schema(description = "기술 카테고리 ID", example = "1")
    private Long techCategoryId;

    @Column(name = "tech_category_name")
    @Schema(description = "카테고리 이름", example = "Backend")
    private String techCategoryName;

    @Column(name = "dev_active")
    @Schema(description = "개발자용. 스택 활성화 여부")
    private boolean devActive;

    @OneToMany(mappedBy = "techCategory")
    @Schema(description = "기술 스택 목록")
    @Builder.Default
    private List<TechStack> techStackList = new ArrayList<>();
}
