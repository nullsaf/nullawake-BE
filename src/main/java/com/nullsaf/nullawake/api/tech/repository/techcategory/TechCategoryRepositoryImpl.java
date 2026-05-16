package com.nullsaf.nullawake.api.tech.repository.techcategory;

import com.nullsaf.nullawake.api.tech.dto.TechCategoryResponse.TechCategoryDto;
import com.nullsaf.nullawake.api.tech.dto.TechStackListResponse;
import com.nullsaf.nullawake.api.tech.entity.QTechCategory;
import com.nullsaf.nullawake.api.tech.entity.QTechStack;
import com.nullsaf.nullawake.api.tech.entity.QUserTechStack;
import com.nullsaf.nullawake.api.tech.entity.TechCategory;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * TechCategoryRepository 구현체
 */
@Repository
@RequiredArgsConstructor
public class TechCategoryRepositoryImpl implements TechCategoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QTechCategory techCategory = QTechCategory.techCategory;
    private final QTechStack techStack = QTechStack.techStack;
    private final QUserTechStack userTechStack = QUserTechStack.userTechStack;

    /**
     * 활성화된 카테고리 조회
     * @return TechCategoryDto
     */
    @Override
    public List<TechCategoryDto> findActiveCategoriesWithStackCount() {

        return queryFactory
            .select(Projections.constructor(
                TechCategoryDto.class,
                techCategory.techCategoryId,
                techCategory.techCategoryName,
                techStack.techStackId.count()
            ))
            .from(techCategory)
            .leftJoin(techCategory.techStackList, techStack)
            .on(techStack.devActive.isTrue())
            .where(techCategory.devActive.isTrue())
            .groupBy(
                techCategory.techCategoryId,
                techCategory.techCategoryName
            )
            .orderBy(techCategory.techCategoryId.asc())
            .fetch();
    }

    /**
     * 카테고리 스택 조회
     * @param techCategoryId 카테고리 ID
     * @param userId 사용자 ID
     * @return TechStackListResponse
     */
    @Override
    public Optional<TechStackListResponse> findTechStacksByCategoryIdAndUserId(
        Long techCategoryId,
        Long userId
    ) {
        TechCategory category = queryFactory
            .selectFrom(techCategory)
            .where(
                techCategory.techCategoryId.eq(techCategoryId),
                techCategory.devActive.isTrue()
            )
            .fetchOne();

        if (category == null) {
            return Optional.empty();
        }

        List<TechStackListResponse.TechStack> techStackList = queryFactory
            .select(Projections.constructor(
                TechStackListResponse.TechStack.class,
                techStack.techStackId,
                techStack.techStackName,
                techStack.description,
                userTechStack.selected.coalesce(false)
            ))
            .from(techStack)
            .leftJoin(userTechStack)
            .on(
                userTechStack.techStack.techStackId.eq(techStack.techStackId),
                userTechStack.user.userId.eq(userId)
            )
            .where(
                techStack.techCategory.techCategoryId.eq(techCategoryId),
                techStack.devActive.isTrue()
            )
            .orderBy(techStack.techStackId.asc())
            .fetch();

        return Optional.of(
            new TechStackListResponse(
                category.getTechCategoryId(),
                category.getTechCategoryName(),
                techStackList
            )
        );
    }
}