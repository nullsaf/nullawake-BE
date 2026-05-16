package com.nullsaf.nullawake.api.tech.repository.usertechstack;

import com.nullsaf.nullawake.api.tech.entity.QUserTechStack;
import com.nullsaf.nullawake.api.tech.entity.UserTechStack;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserTechStackRepositoryImpl implements UserTechStackRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    private final QUserTechStack userTechStack = QUserTechStack.userTechStack;

    @Override
    public long deleteByUserIdAndCategoryId(Long userId, Long categoryId) {
        return queryFactory
            .delete(userTechStack)
            .where(
                userTechStack.user.userId.eq(userId),
                userTechStack.techStack.techCategory.techCategoryId.eq(categoryId)
            )
            .execute();
    }

    @Override
    public List<UserTechStack> findByUserIdAndTechStackIds(
        Long userId,
        List<Long> techStackIds
    ) {
        if (techStackIds == null || techStackIds.isEmpty()) {
            return List.of();
        }

        return queryFactory
            .selectFrom(userTechStack)
            .where(
                userTechStack.user.userId.eq(userId),
                userTechStack.techStack.techStackId.in(techStackIds)
            )
            .fetch();
    }
}
