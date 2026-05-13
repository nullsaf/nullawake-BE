package com.nullsaf.nullawake.api.tech.repository;

import com.nullsaf.nullawake.api.tech.dto.TechCategoryResponse;
import com.nullsaf.nullawake.api.tech.dto.TechCategoryResponse.TechCategory;
import com.nullsaf.nullawake.api.tech.entity.QTechCategory;
import com.nullsaf.nullawake.api.tech.entity.QTechStack;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
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

    @Override
    public List<TechCategory> findActiveCategoriesWithStackCount() {

        return queryFactory
            .select(Projections.constructor(
                TechCategoryResponse.TechCategory.class,
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
}
