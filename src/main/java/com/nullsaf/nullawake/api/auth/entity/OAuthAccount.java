package com.nullsaf.nullawake.api.auth.entity;

import com.nullsaf.nullawake.api.user.entity.Users;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "OAUTH_ACCOUNT",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UK_OAUTH_PROVIDER_USER",
                        columnNames = {"provider", "provider_user_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OAuthAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "oauth_id")
    private Long oauthId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    private OAuthProvider provider;

    @Column(name = "provider_user_id", nullable = false)
    private String providerUserId;

    @Column(name = "refresh_token")
    private String refreshTokenHash;

    public static OAuthAccount create(
            Users user,
            OAuthProvider provider,
            String providerUserId,
            String refreshTokenHash
    ) {
        return OAuthAccount.builder()
                .user(user)
                .provider(provider)
                .providerUserId(providerUserId)
                .refreshTokenHash(refreshTokenHash)
                .build();
    }

    public void updateRefreshTokenHash(String refreshTokenHash) {
        this.refreshTokenHash = refreshTokenHash;
    }
}
