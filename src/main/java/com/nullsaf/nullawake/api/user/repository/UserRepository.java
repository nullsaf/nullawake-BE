package com.nullsaf.nullawake.api.user.repository;

import com.nullsaf.nullawake.api.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByUserIdAndDeletedAtIsNull(Long userId);
}
