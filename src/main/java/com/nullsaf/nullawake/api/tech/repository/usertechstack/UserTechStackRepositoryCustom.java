package com.nullsaf.nullawake.api.tech.repository.usertechstack;

import com.nullsaf.nullawake.api.tech.entity.UserTechStack;
import java.util.List;

public interface UserTechStackRepositoryCustom {
    long deleteByUserIdAndCategoryId(Long userId, Long categoryId);

    List<UserTechStack> findByUserIdAndTechStackIds(Long userId, List<Long> techStackIds);
}
