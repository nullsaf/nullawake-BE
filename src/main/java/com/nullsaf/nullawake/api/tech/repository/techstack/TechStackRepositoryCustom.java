package com.nullsaf.nullawake.api.tech.repository.techstack;

import com.nullsaf.nullawake.api.tech.entity.TechStack;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface TechStackRepositoryCustom {

    // 사용자 IDfh
    List<TechStack> findActiveTechStacksByIdsAndCategoryId(List<Long> techStackIds, Long categoryId);

}
