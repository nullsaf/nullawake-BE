package com.nullsaf.nullawake.api.tech.repository;

import com.nullsaf.nullawake.api.tech.entity.TechCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TechCategoryRepository extends JpaRepository<TechCategory, Long>, TechCategoryRepositoryCustom {
}
