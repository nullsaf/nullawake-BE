package com.nullsaf.nullawake.api.tech.repository.techstack;

import com.nullsaf.nullawake.api.tech.entity.TechStack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TechStackRepository extends JpaRepository<TechStack, Long>, TechStackRepositoryCustom {
}
