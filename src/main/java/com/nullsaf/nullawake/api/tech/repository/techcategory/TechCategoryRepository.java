package com.nullsaf.nullawake.api.tech.repository.techcategory;

import com.nullsaf.nullawake.api.tech.entity.TechCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TechCategoryRepository extends JpaRepository<TechCategory, Long>, TechCategoryRepositoryCustom {

}
