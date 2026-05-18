package com.nullsaf.nullawake.api.tech.repository.usertechstack;

import com.nullsaf.nullawake.api.tech.entity.UserTechStack;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTechStackRepository extends JpaRepository<UserTechStack, Long>, UserTechStackRepositoryCustom {
    /**
     * 유저가 특정 기술 스택을 선택했는지 여부를 확인하는 메서드
     *
     * @param userId
     * @param techStackId
     * @return
     */
    boolean existsByUserUserIdAndTechStackTechStackIdAndSelectedTrue(
        Long userId,
        Long techStackId
    );
}
