package com.nullsaf.nullawake.api.auth.repository;

import com.nullsaf.nullawake.api.auth.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Long> {
}
