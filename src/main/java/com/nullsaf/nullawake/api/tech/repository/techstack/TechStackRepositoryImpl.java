package com.nullsaf.nullawake.api.tech.repository.techstack;

import com.nullsaf.nullawake.api.tech.entity.QTechStack;
import com.nullsaf.nullawake.api.tech.entity.TechStack;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TechStackRepositoryImpl implements TechStackRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    private final QTechStack techStack = QTechStack.techStack;

    @Override
    public List<TechStack> findActiveTechStacksByIdsAndCategoryId(
        List<Long> techStackIds,
        Long categoryId
    ) {
        if (techStackIds == null || techStackIds.isEmpty()) {
            return List.of();
        }

        return queryFactory
            .selectFrom(techStack)
            .where(
                techStack.techStackId.in(techStackIds),
                techStack.techCategory.techCategoryId.eq(categoryId),
                techStack.devActive.isTrue()
            )
            .fetch();
    }
}
