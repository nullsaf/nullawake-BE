package com.nullsaf.nullawake.api.auth.repository;

import com.nullsaf.nullawake.api.auth.entity.OAuthAccount;
import com.nullsaf.nullawake.api.auth.entity.OAuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OAuthAccountRepository extends JpaRepository<OAuthAccount, Long> {

    Optional<OAuthAccount> findByProviderAndProviderUserId(
            OAuthProvider provider,
            String providerUserId
    );

}
