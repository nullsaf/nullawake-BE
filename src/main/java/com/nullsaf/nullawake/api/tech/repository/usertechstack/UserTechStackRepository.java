package com.nullsaf.nullawake.api.tech.repository.usertechstack;

import com.nullsaf.nullawake.api.tech.entity.UserTechStack;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTechStackRepository extends JpaRepository<UserTechStack, Long>, UserTechStackRepositoryCustom {

}
