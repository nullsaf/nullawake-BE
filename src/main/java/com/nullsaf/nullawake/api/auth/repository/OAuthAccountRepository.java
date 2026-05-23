package com.nullsaf.nullawake.api.auth.repository;

import com.nullsaf.nullawake.api.auth.entity.OAuthAccount;
import com.nullsaf.nullawake.api.auth.entity.OAuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OAuthAccountRepository extends JpaRepository<OAuthAccount, Long> {

    // DB에 기존 계정이 있는지 확인하는 메서드
    Optional<OAuthAccount> findByProviderAndProviderUserId(
            OAuthProvider provider,
            String providerUserId
    );

    Optional<OAuthAccount> findByUser_UserId(Long userId);
}
