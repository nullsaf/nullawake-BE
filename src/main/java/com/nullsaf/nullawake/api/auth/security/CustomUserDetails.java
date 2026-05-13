package com.nullsaf.nullawake.api.auth.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Spring Security 인증 객체에 저장되는 사용자 정보 클래스.
 *
 * 현재는 userId만 관리
 */
@Getter
public class CustomUserDetails implements UserDetails {

    private final Long userId;

    /**
     * 인증된 사용자 정보를 생성한다.
     *
     * @param userId 사용자 ID
     */
    public CustomUserDetails(Long userId) {
        this.userId = userId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return String.valueOf(userId);
    }
}
