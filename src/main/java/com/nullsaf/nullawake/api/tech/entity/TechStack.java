package com.nullsaf.nullawake.api.tech.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TECH_STACK")
@Schema(description = "기술 스택")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TechStack {

    @Id
    @Column(name = "tech_stack_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "기술 스택 ID", example = "1")
    private Long techStackId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tech_category_id", nullable = false)
    @Schema(description = "기술 카테고리")
    private TechCategory techCategory;

    @Column(name = "tech_stack_name")
    @Schema(description = "기술 스택 이름", example = "Spring Boot")
    private String techStackName;

    @Column(name = "description")
    @Schema(description = "기술 스택 설명", example = "Java 기반 웹 프레임워크")
    private String description;

    @Column(name = "dev_active")
    @Schema(description = "개발자용 여부", example = "true")
    private boolean devActive;
}
