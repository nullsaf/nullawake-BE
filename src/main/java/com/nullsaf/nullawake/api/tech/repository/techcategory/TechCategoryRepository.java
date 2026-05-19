package com.nullsaf.nullawake.api.tech.repository.techcategory;

import com.nullsaf.nullawake.api.tech.entity.TechCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TechCategoryRepository extends JpaRepository<TechCategory, Long>, TechCategoryRepositoryCustom {

    // 활성 카테고리 조회
    List<TechCategory> findByDevActiveTrueOrderByTechCategoryIdAsc();
}
