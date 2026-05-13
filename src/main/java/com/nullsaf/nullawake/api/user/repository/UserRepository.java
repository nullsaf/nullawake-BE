package com.nullsaf.nullawake.api.user.repository;

import com.nullsaf.nullawake.api.user.dto.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Long> {
}
